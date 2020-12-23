## Spring Study


### 스프링 구조 살펴보기
- MyProject/src/main/resources/**application.properties**


### IoC (Inversion of Control) : 제어의 역전
- 쉽게 생각해서 객체의 생성 및 관리의 주도권이 각 컨트롤러가 가지는 것이 아니라 Spring F/W가 가지는 것이다.
- new 키워드로 멤버변수의 객체를 생성해주지 않고, Controller 빈이 생성될 때 DI(Dependency Injection) 되어 사용가능해진다.
- **IoC Container**
  - 제어의 역전이 가능하도록 각 객체들의 생성 및 관리를 주관하는 컨테이너
  - 빈(Bean, 스프링이 관리하는 객체)을 생성, 제공
  - ApplicationContext 또는 BeanFactory 중의 하나를 컨테이너로 사용한다. (Applicationcontext 는 BeanFactory 를 상속)
  - 모든 클래스가 Bean으로 등록되어 있지는 않다.
  - 객체(Bean) 은 한번만 생성되어서 application context 에 의해 한 번만 생성되어 재사용 된다 (Singleton scope 의 객체)
  - MultiThread 상황에서 Singleton scope 의 객체를 구현하는 것은 굉장히 번거로운 일이지만, (공유자원, Syncronized 문제) IoC 컨테이너의 빈을 등록해서 가져다가 사용하는 식으로 사용하면 편하게 Singleton scope 의 객체의 생성을 할 수 있다.


### Bean
- IoC Container 가 관리하는 객체
- 만약 new 키워드로 인스턴스를 생성하여 객체를 관리한다면 그것은 Bean이 아니다.
- Bean 만 의존성 주입이 가능하다.
- **Bean 으로 만드는 방법**
  1. Component Scan
      - Annotation 활용
      - 최상단 메인 함수의 @SpringBootApplication 부터 @componentScan 을 포함하기 때문에 해당 파일 하위의 모든 클래스들을 스캔한다. (@Controller, @Component 등등) Bean 관련 Annotation 들이 붙어 있는 클래스를 모두 찾아서 Bean 등록함.
      - @Repository, @Service, @Controller, @Configuration
      - 직접 등록하지 않더라도 스프링이 실행될 때 component scan 과정을 통해 Bean Annotation 처리된 클래스들을 모두 등록한다.
  2. 직접 XML 이나 자바 설정 파일에 등록
      - 자바 설정 파일을 작성한다.
      ```java
      @Configuration // 이 Config 파일 자체는 Configuration 타입 빈
      public class SampleConfig {
    	  @Bean // 직접 객체를 생성하여 어노테이션을 통해 Bean으로 만듬.
	      public SampleController sampleController() {
		      return new SampleController();
    	  }
      }
      ```
- Bean은 ApplicationContext에서 getBean 으로 가져올 수도 있지만, 보통은 @Autowired 라는 어노테이션을 통해 Bean 을 멤버변수로 가저와서 사용한다.


### DI (Dependency Injection)
- @Autowired
- 어노테이션 선언 부위 : 멤버변수, 생성자, Setter
- 생성자는 Spring 4.3 부터 @Autowired 선언을 생략해도 된다.
- 스프링 권장은 생성자를 통한 DI
```java
// 1. 멤버변수에 @Autowired 
@Controller
public class SampleController {

  @Autowired
  private SampleObject sampleObject;

}
```
```java
// 2. 생성자 - 스프링 권장
@Controller
public class SampleController {

  private SampleObject sampleObject;

  @Autowired // 4.3부터 생략 가능
  public SampleController(SampleObject sampleObject) {
    this.sampleObject = sampleObject;
  }

}
```
```java
// 3. Setter
@Controller
public class SampleController {

  private SampleObject sampleObject;

  @Autowired
  public void setSampleObject(SampleObject sampleObject){
    this.sampleObject = sampleObject;
  }

}
```
- 생성자를 사용하는 방법이 좋은 이유 : 필수적으로 사용해야 하는 객체 없이는 인스턴스 자체를 생성하지 못하게 할 수 있다. (위의 예시에서 생성자를 사용하게 되면 SampleObject 가 없으면 SampleController 빈 생성 자체가 불가능해진다.)
- 생성자를 사용할 때 위험 : **순환참조 발생 가능.** (만약 SampleObject 를 생성하기 위해서 SampleController 객체가 주입이 되어야 한다면, 서로 생성되기 위해 서로의 객체가 필요한 상황이기 때문에 두 객체 모두 생성될 수 없는 상황에 직면) -> 이 경우에는 Autowired 변수, Setter 등을 사용한다.
- 가급적이면 순환참조가 발생하지 않게 끔.


### AOP (관점 지향 프로그래밍)
```java
// 1. 흩어진 doSomethingA 와 doSomethingB
class A {
  public void a() {
    doSomethingA();
    System.out.println("MethodA");
    doSomethingB();
  }
  public void b() {
    doSomethingA();
    System.out.println("MethodB");
    doSomethingB();
  }
}
class B {
  public void c() {
    doSomethingA();
    System.out.pringln("MethodC");
    doSomethingB();
  }
}

// 2. 모아 놓은 doSomethingA 와 doSomethingB
class A {
  public void a() {
    System.out.println("MethodA");
  }
  public void b() {
    System.out.println("MethodB");
  }
}
class B {
  public void c() {
    System.out.println("MethodC");
  }
}
class ABC {
  public void doSomethingAB(JoinPoint point){
    doSomethingA();
    point.execute();
    doSomethingB();
  }
}
```
- doSomethingA, doSomethingB 를 별도의 클래스와 메소드로 빼내어 유지보수를 용이하게 함. -> AOP
- 핵심 내용 : **기능을 "비즈니스 기능", "공통 기능" 으로 구분하고, 공통 기능을 개발자의 비즈니스 코드 밖에서 필요한 시점에 적용하는 프로그래밍 방식**
- **다양한 AOP 구현 방법**
  1. 컴파일
    - A.java -> (AOP) -> A.class 사이에 컴파일 도중 코드를 삽입(AspectJ)
  2. 바이트코드 조작
    - A.java -> A.class -> (AOP) -> JVM Memory
    - .class 파일이 class loader 에 의해 JVM 에 올라가기 직전 바이트코드를 추가하여 메모리에 올림.
  3. 프록시 패턴
    - 스프링AOP가 사용하는 방법
    

### Proxy Pattern (스프링AOP)
- 빈을 생성하면 자동으로 프록시 또한 생성되어 빈을 사용했을때 프록시가 대신 사용되어서 다양한 앞 뒤의 로그추적이나 성능추적 옵션이 사용 가능해진다.
- @Transactional : commit / rollback 에 관련한 코드를 직접 넣지 않고 생략할 수 있다., @Transactional 도 프록시패턴을 사용해서 구현되어 있음.
```java
@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets")
@Transactional(readOnly = true)
Owner findByid(@Param("id") Integer id);
```


### AOP 적용 예제
- 메소드 처리 시간 로깅해주는 어노테이션 만들기
```java
// 1. @LogExecutionTime 어노테이션 정의
@Target(ElementType.METHOD)         // 어노테이션 타겟 : 메소드
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 지속 : 런타임
public @interface LogExecutionTime {

}

```
```java
// 2. LogAspect 클래스 구현
@Component  // Bean 등록
@Aspect     // AspectJ 라이브러리 사용
public class LogAspect {

  Logger logger = LoggerFactory.getLogger(LogAspect.class);

  // @Around 를 통해 LogExecutionTime 어노테이션이 붙은 메소드를
  // ProceedingJoinPoint(AspectJ) 를 통해 받아서 처리한다. 
  @Around("@annotation(LogExecutionTime)")
  public Object LogExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    // 실제 메소드 실행
    Object proceed = joinPoint.proceed();

    stopWatch.stop();
    logger.info(stopWatch.prettyPrint());

    return proceed;
  }
}
```
```java
// 3. LogExecutionTime 어노테이션 적용
@GetMapping("/owners/new")
@LogExecutionTime
public String newOwner(Owner owner){
  // ~ 이하 생략
}
```


### PSA (Portable Service Abstraction)
- 