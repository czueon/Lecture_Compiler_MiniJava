# 2장 실습 보고서 — MiniJava 어휘 분석기

## 실습 목표

JavaCC를 사용하여 MiniJava 언어의 어휘 분석기(Lexer)를 작성한다.
`MiniJavaLexer.jj`에 토큰 명세를 작성하고, JavaCC로 렉서 코드를 자동 생성한 후
8개의 예제 MiniJava 프로그램에 대해 어휘 분석을 성공적으로 수행하는 것이 목표이다.

---

## 프로젝트 구조

```
AndrewAppel/chap2/javacc/
├── MiniJavaLexer.jj       # 렉서 명세
├── Main.java              # 어휘 분석기 실행 진입점
└── javacc-javacc-7.0.13/  # JavaCC 설치 디렉토리
```

javacc 실행 후 자동 생성되는 파일:
- `MiniJavaParser.java`, `MiniJavaParserTokenManager.java`, `MiniJavaParserConstants.java`
- `Token.java`, `TokenMgrError.java`, `ParseException.java`, `JavaCharStream.java`

---

## 토큰 명세 (MiniJavaLexer.jj)

### SKIP — 무시할 문자
공백, 탭, 개행을 토큰으로 인식하지 않고 건너뛴다.

```
SKIP : { " " | "\t" | "\r" | "\n" }
```

### SPECIAL_TOKEN — 주석
`//` 한 줄 주석과 `/* */` 여러 줄 주석을 처리한다.

```
SPECIAL_TOKEN : {
  <SINGLE_LINE_COMMENT: "//" (~["\n","\r"])* ("\n"|"\r"|"\r\n")>
| <MULTI_LINE_COMMENT: "/*" (~["*"])* "*" (~["/"] (~["*"])* "*")* "/">
}
```

### TOKEN — 키워드, 식별자, 정수 리터럴

키워드(`class`, `public`, `static` 등 19개)를 `IDENTIFIER`보다 먼저 정의하여
키워드가 식별자로 매칭되는 것을 방지한다.

| 토큰 | 설명 |
|---|---|
| `<CLASS>`, `<PUBLIC>`, `<STATIC>`, `<VOID>`, `<MAIN>`, `<STRING>`, `<EXTENDS>`, `<RETURN>`, `<INT>`, `<BOOLEAN>`, `<IF>`, `<ELSE>`, `<WHILE>`, `<PRINTLN>`, `<LENGTH>`, `<TRUE>`, `<FALSE>`, `<THIS>`, `<NEW>` | MiniJava 키워드 19개 |
| `<#DIGIT>` | `["0"-"9"]`에 이름을 붙인 매크로. 실제 토큰으로 생성되지 않고 `INTEGER_LITERAL` 정의 내에서만 참조됨 |
| `<IDENTIFIER>` | 문자로 시작하는 식별자 (`[a-zA-Z][a-zA-Z0-9_]*`) |
| `<INTEGER_LITERAL>` | `<DIGIT>` 매크로를 활용한 10진수 정수 (`(<DIGIT>)+`) |

### 파서 규칙 — 렉서 테스트용

```
void Goal() : { } { ( MiniJavaToken() )* <EOF> }
```

`MiniJavaToken()`에서 MiniJava의 모든 토큰을 나열하여 입력 파일 전체를 순차적으로 어휘 분석한다.

---

## 파일별 구현 내용

### MiniJavaLexer.jj
렉서 명세 파일. SKIP, SPECIAL_TOKEN, TOKEN 규칙과 테스트용 파서 규칙(`Goal`, `MiniJavaToken`)을 포함한다.

### Main.java
표준 입력을 받아 `MiniJavaParser`로 어휘 분석을 수행한다.
성공 시 `Lexical analysis successfull`, 실패 시 에러 메시지를 출력한다.

---

## 실행 방법

> cf. `out/` 폴더는 컴파일 결과물(.class)이 저장되는 디렉토리이다.

`AndrewAppel/chap2/javacc/` 디렉토리에서:

```bash
# 1. JavaCC로 렉서 코드 생성
javacc MiniJavaLexer.jj

# 2. 컴파일
javac -d ../../../out/chap2 *.java

# 3. 실행 (예: Factorial.java)
java -cp ../../../out/chap2 Main < ../../programs/Factorial.java
```

---

## 실행 결과

8개의 예제 프로그램 모두 어휘 분석 성공:

```
BinarySearch.java : Lexical analysis successfull
BinaryTree.java   : Lexical analysis successfull
BubbleSort.java   : Lexical analysis successfull
Factorial.java    : Lexical analysis successfull
LinearSearch.java : Lexical analysis successfull
LinkedList.java   : Lexical analysis successfull
QuickSort.java    : Lexical analysis successfull
TreeVisitor.java  : Lexical analysis successfull
```
