import ast.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Interp {

    public static void main(String[] args) {
        // a := 5 + 3
        Stm stm1 = new AssignStm("a",
            new OpExp(new NumExp(5), OpExp.Plus, new NumExp(3)));

        // b := (print(a, a-1), 10 * a)
        Stm stm2 = new AssignStm("b",
            new EseqExp(
                new PrintStm(
                    new PairExpList(new IdExp("a"),
                        new LastExpList(
                            new OpExp(new IdExp("a"), OpExp.Minus, new NumExp(1))))),
                new OpExp(new NumExp(10), OpExp.Times, new IdExp("a"))));

        // print(b)
        Stm stm3 = new PrintStm(
            new LastExpList(new IdExp("b")));

        Stm prg = new CompoundStm(stm1, new CompoundStm(stm2, stm3));

        interpStm(prg, new HashMap<String, Integer>());
    }

    private static void interpStm(Stm stm, HashMap<String, Integer> env) {
        if (stm instanceof CompoundStm) {
            CompoundStm cstm = (CompoundStm) stm;
            interpStm(cstm.stm1, env);
            interpStm(cstm.stm2, env);
        } else if (stm instanceof AssignStm) {
            AssignStm astm = (AssignStm) stm;
            env.put(astm.id, interpExp(astm.exp, env));
        } else if (stm instanceof PrintStm) {
            PrintStm pstm = (PrintStm) stm;
            ArrayList<Integer> values = interpExpList(pstm.exps, env);
            for (int i = 0; i < values.size(); i++) {
                if (i > 0) System.out.print(" ");
                System.out.print(values.get(i));
            }
            System.out.println();
        } else {
            System.err.println("Unknown statement");
        }
    }

    private static Integer interpExp(Exp exp, HashMap<String, Integer> env) {
        if (exp instanceof IdExp) {
            IdExp iexp = (IdExp) exp;
            return env.get(iexp.id);
        } else if (exp instanceof NumExp) {
            NumExp nexp = (NumExp) exp;
            return nexp.num;
        } else if (exp instanceof OpExp) {
            OpExp oexp = (OpExp) exp;
            int left = interpExp(oexp.left, env);
            int right = interpExp(oexp.right, env);
            switch (oexp.oper) {
                case OpExp.Plus:  return left + right;
                case OpExp.Minus: return left - right;
                case OpExp.Times: return left * right;
                case OpExp.Div:   return left / right;
                default: throw new RuntimeException("Unknown operator");
            }
        } else if (exp instanceof EseqExp) {
            EseqExp eexp = (EseqExp) exp;
            interpStm(eexp.stm, env);
            return interpExp(eexp.exp, env);
        } else {
            System.err.println("Unknown expression");
            return 0;
        }
    }

    private static ArrayList<Integer> interpExpList(ExpList expList, HashMap<String, Integer> env) {
        ArrayList<Integer> results = new ArrayList<>();
        if (expList instanceof PairExpList) {
            PairExpList plist = (PairExpList) expList;
            results.add(interpExp(plist.head, env));
            results.addAll(interpExpList(plist.tail, env));
        } else if (expList instanceof LastExpList) {
            LastExpList llist = (LastExpList) expList;
            results.add(interpExp(llist.head, env));
        } else {
            System.err.println("Unknown expr list");
        }
        return results;
    }
}
