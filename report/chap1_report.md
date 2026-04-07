# 1장 실습 보고서 — Straight-line 프로그래밍 언어 인터프리터

## 실습 목표

분기(if/while)가 없는 간단한 언어인 Straight-line 프로그래밍 언어의 인터프리터를 구현한다.
언어의 문법 구조를 AST(Abstract Syntax Tree) 클래스로 표현하고, 이를 순회하며 실행하는 `Interp.java`를 완성하는 것이 목표이다.

---

## 프로젝트 구조

```
AndrewAppel/chap1/
├── ast/
│   ├── Stm.java
│   ├── CompoundStm.java
│   ├── AssignStm.java
│   ├── PrintStm.java
│   ├── Exp.java
│   ├── IdExp.java
│   ├── NumExp.java
│   ├── OpExp.java
│   ├── EseqExp.java
│   ├── ExpList.java
│   ├── PairExpList.java
│   └── LastExpList.java
└── Interp.java
```

---

## 언어 문법

| 문법 | AST 클래스 |
|---|---|
| `Stm ; Stm` | `CompoundStm` |
| `id := Exp` | `AssignStm` |
| `print(ExpList)` | `PrintStm` |
| `id` | `IdExp` |
| `num` | `NumExp` |
| `Exp Binop Exp` | `OpExp` |
| `(Stm, Exp)` | `EseqExp` |
| `Exp , ExpList` | `PairExpList` |
| `Exp` | `LastExpList` |

---

## 파일별 구현 내용

### ast/Stm.java
문장을 나타내는 추상 클래스.

### ast/CompoundStm.java
두 문장을 순서대로 실행하는 복합 문장 (`stm1 ; stm2`).

### ast/AssignStm.java
변수에 식의 결과를 대입하는 문장 (`id := exp`).

### ast/PrintStm.java
식 목록을 공백으로 구분하여 출력하는 문장 (`print(exps)`).

### ast/Exp.java
식을 나타내는 추상 클래스.

### ast/IdExp.java
변수를 참조하는 식. `env`에서 변수 이름으로 값을 조회한다.

### ast/NumExp.java
정수 리터럴 식.

### ast/OpExp.java
이항 연산식 (`+`, `-`, `*`, `/`). 연산자는 `Plus=1`, `Minus=2`, `Times=3`, `Div=4` 상수로 정의.

### ast/EseqExp.java
문장을 실행한 후 식을 평가하여 값을 반환하는 식 (`(stm, exp)`).

### ast/ExpList.java
print의 인자 목록을 나타내는 추상 클래스.

### ast/PairExpList.java
인자가 2개 이상일 때 사용. `head`(Exp)와 `tail`(ExpList)로 구성.

### ast/LastExpList.java
인자 목록의 마지막 원소. `head`(Exp) 하나만 보유.

### Interp.java
인터프리터 본체.

- `main()` — 예제 프로그램을 AST로 직접 구성하고 실행
- `interpStm(Stm, HashMap)` — 문장 실행. `instanceof`로 분기하여 처리
- `interpExp(Exp, HashMap)` — 식을 평가하고 정수 반환
- `interpExpList(ExpList, HashMap)` — print 인자 목록을 평가하여 `ArrayList<Integer>` 반환
- `env` (`HashMap<String, Integer>`) — 변수 이름과 값을 저장하는 환경

---

## 실행 예제

`main()`에서 아래 프로그램을 AST로 구성하여 실행한다.

```
a := 5 + 3;
b := (print(a, a-1), 10 * a);
print(b)
```

실행 흐름:
1. `a := 5 + 3` → env: `{a=8}`
2. `print(a, a-1)` → `8 7` 출력
3. `b := 10 * a` → env: `{a=8, b=80}`
4. `print(b)` → `80` 출력

---

## 실행 방법

> cf. `out/` 폴더는 컴파일 결과물(.class)이 저장되는 디렉토리이다.

`AndrewAppel/chap1/` 디렉토리에서:

```bash
javac -d ../../out ast/*.java Interp.java
java -cp ../../out Interp
```

## 실행 결과

```
8 7
80
```
