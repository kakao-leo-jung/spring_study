## JAVA 기초 동작 원리 스터디

### JVM, JDK, JRE
- **JVM (Java Virtual Machine)**
  - 자바 가상 머신, 자바의 바이트코드(.class) 파일을 OS 에 특화된 코드로 변환(인터프리터 / JIT 컴파일러)하여 실행
  - 바이트 코드를 실행하는 표준 구현체
  - **바이트 코드**
  ```java
  // 1. Hello.java
  public class Hello {
    public static void main(String[] args){
      System.out.println("Hello Java!");
    }
  }
  ```
  ```java
  $ javac Hello.java
  $ ls
  Hello.java  Hello.class
  $ javap -c Hello
  Compiled from "Hello.java"
  public class Hello {
    public Hello();
      Code:
        0: aload_0
        1: invokespecial  #1    // Method java/lang/Object."<init>":()V
        4: return

    public static void main(java.lang.String[]);
      Code:
        0: getstatic      #7    // Field java/lang/System.out:Ljava/io/PrintStream;
        3: Idc            #13   // String Hello Java!
        5: invokespecial  #15   // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        8: return
  }
  ```
  - 바이트 코드를 실행 (인터프리터, JIT) 를 통해 네이티브OS 에 맞는 머신 코드로 변환되어 실행 됨.
  - 표준 JVM 스팩에 맞춰 다양한 밴더사가 JVM 개발(오라클, 아마존 등등)
  - 특정 플랫폼에 종속적 : JVM 자체는 바이트코드를 읽어들여 다시 인터프리터 또는 JIT를 통해 네이티브OS 에 맞는 머신 코드로 변환시켜야 하기 때문에 OS 플랫폼에 종속된다.


- **JRE (Java Runtime Environment)**
  - 자바를 실행하는데 필요한 배포판
  - JVM + Library 로 이루어져 있다.
  - JVM 과 핵심 라이브러리 및 런타임 환경에서 사용하는 property 세팅이나 리소스 파일을 가지고 있다.
  - 개발 도구는 포함X (예: javac 같은 컴파일도구는 JRE에 미포함, 오로지 실행환경만을 위해 구성되어 있음)


- **JDK (Java Developement Kit)**
  - JRE + 개발에 필요한 툴
  - 소스 코드를 작성할 때 사용하는 JAVA 자체는 플랫폼 독립적.
  - Oracle Java 11 부터는 JDK 개발 툴만 제공, JRE는 따로 제공X
  - Java Module System : Java9 부터 Jigsaw 프로젝트 도임, 유연한 런타임 이미지를 만들 수 있도록 자바 플랫폼을 경량화, JRE를 직접 구성 및 경량화하여 배포 가능.


- **자바의 유료화?**
  - 오라클에서 만든 Oracle JDK 11 버전부터 상용으로 사용할 때 유료.
  - Corretto(Amazon) JDK, Open JDK 등은 여전히 무료


### JVM 구조
- 클래스 로더 시스템(Class Loader)
  - 로딩, 링크, 초기화
  - .class 에서 바이트코드를 읽고 메모리에 저장
  - 로딩 : .class 를 읽어오는 과정
  - 링크 : 레퍼런스를 연결하는 과정
  - 초기화 : static 값들 초기화 및 변수에 할당

- 메모리
  - 스택, 힘, 메소드, 네이티드메소드 스택, PC
  - **메소드 영역** : 클래스 수준의 정보 저장(클래스 이름, 부모 클래스 이름, 메소드, 변수, 패키지 등), 공유될 수 있는 자원.
  - **힙 영역** : 인스턴스화(new) 된 객체를 저장, 공유될 수 있는 자원.
  - **스택 영역** : 쓰레드 마다 런타임 스택 생성, 메소드 호출을 스택 프레임(Method Call)이라 불리는 블럭으로 쌓음, 쓰레드가 종료되면 런타임 스택도 사라짐
  - **PC(Program Counter)** : 각 쓰레드 마다 현재 실행되고 있는 스택프레임의 위치를 가리키는 포인터
  - **네이티브 메소드 스택** : 쓰레드 마다 생성, 네이티브 메소드 호출할 때 마다 사용하는 별도의 메소드 스택
  - 네이티브 메소드 : Java 가 아닌 다른 네이티브 언어로 작성된 메소드
    - ex:) public static **native** Thread currentThread();
    - JNI (Java Native Interface), C, C++, 어셈블리 등으로 작성된 함수를 사용할 수 있음.

- 실행 엔진
  - 인터프리터, JIT, GC
  - 인터프리터 : 바이트 코드를 **한 줄씩** 실행
  - JIT Compiler : 인터프리터 효율을 높이기 위해 인터프리터가 반복되는 코드를 발견하면 JIT는 네이티브 코드로 바꿔놓고, 다음 인터프리터가 동일한 코드를 발견 했을 때 사전에 있던 네이티브 코드를 가져와서 실행, 인터프리터 동작을 최적화.
  - GC (Garbage Collector) : 참조되지 않는 객체를 모아서 제거


### 클래스 로더 (Class Loader)
- 로딩 -> 링크 -> 초기화 순으로 진행
- 로딩
  - 클래스 로더가 .class 파일을 읽고, 그 내용에 따라 적절한 바이너리 데이터를 만들고 **메소드 영역**에 저장
  - 이때 메소드 영역에 저장되는 데이터
    - FQCN(Fully Qualfied Class Name)
    - Class / Interface / Enum
    - 메소드 / 변수
  - 로딩이 끝나면 해당 클래스 타입의 **Class 객체**를 생성하여 **힙 영역**에 저장
  ```java
  // Class 타입의 인스턴스
  public class Whiteship {
    public void work(){
      Whiteship whiteship = new Whiteship();
      whiteship.getClass();   // Class 타입의 객체가 만들어 짐.
      // Class<Whiteship> : 최초 클래스 로더에 의해 로딩된 바이트 코드들이 메소드 영역에 저장 되고 힙에는 Class 타입으로 메모리에 올라감.
    }
  }
  ```
- 클래스로더 확인해보기
```java
// 클래스 로더 호출
public class App {
  public static void main(String[] args){
    ClassLoader classLoader = App.class.getClassLoader();
    System.out.println(classLoader);
    System.out.println(classLoader.getParent());
    System.out.println(classLoader.getParent().getParent());
  }
}

// 결과
// jdk.internal.loader.ClassLoaders$AppClassLoader@2f0e140b
// jdk.internal.loader.
// ClassLoaders$PlatformClassLoader@6d03e736
// null

// AppClassLoader, PlatformClassLoader 확인 가능
// 최상위 BootstrapClassLoader 의 경우에는 네이티브 코드로 구현되어 있어 자바에서는 확인 불가능.
```