package ru.chernyukai.projects.dating.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class GetProfilesLoggingAspect {

    @Pointcut("execution(public * ru.chernyukai.projects.dating.controller.ProfileController.getAllProfiles(..))")
    public void callGetAllProfiles(){}

    @Around("callGetAllProfiles()")
    public Object aroundCallGetAllProfiles(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        log.info("User {} completed {} for {} ms", SecurityContextHolder.getContext().getAuthentication().getName(), proceedingJoinPoint.getSignature().getName(), timeDelta);
        return result;
    }
}
