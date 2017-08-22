package com.github.jerrysearch.tns.feedback.aspectj;

public aspect ConstructorNew extends AbstractAspectj{

    public pointcut constructorNew(): execution(public com.github.jerrysearch.tns.feedback.Hello.new());

    public pointcut constructorNew2(): execution(public com.github.jerrysearch.tns.feedback.Hello.new(Long));

    public pointcut classInit(): staticinitialization(com.github.jerrysearch.tns.feedback.Hello);

    before(): constructorNew(){
        this.log.info("#constructorNew : {}", thisJoinPointStaticPart);
    }

    before(): constructorNew2(){
        this.log.info("#constructorNew2 : {}", thisJoinPointStaticPart);
    }

    before(): classInit(){
        this.log.info("#classInit: {}", thisJoinPointStaticPart);
    }
}
