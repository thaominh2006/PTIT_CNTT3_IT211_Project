package ra.edu.it211_project.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ra.edu.it211_project.dto.request.GradeRequest;
import ra.edu.it211_project.dto.response.SubmissionResponse;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * @Before - Log tất cả request vào Controller
     */
    @Before("execution(* ra.edu.it211_project.controller..*(..))")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        log.info("[AOP] @Before - Entering: {}.{}() with arguments: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    /**
     * @AfterReturning - Log sau khi chấm điểm thành công (UC-04)
     */
    @AfterReturning(
            pointcut = "execution(* ra.edu.it211_project.service.impl.GradingServiceImpl.gradeSubmission(..))",
            returning = "result"
    )
    public void logAfterGrading(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof GradeRequest gradeRequest) {
            log.info("[AOP] @AfterReturning - Lecturer graded Submission ID: {} with Score: {}",
                    gradeRequest.getSubmissionId(), gradeRequest.getScore());
        }
        if (result instanceof SubmissionResponse submission) {
            log.info("[AOP] Grade Result - Submission ID: {}, Student: {}, Course: {}, Score: {}, Status: {}",
                    submission.getId(),
                    submission.getStudentName(),
                    submission.getCourseName(),
                    submission.getScore(),
                    submission.getStatus());
        }
    }

    /**
     * @AfterReturning - Log sau khi đăng ký khóa học thành công (FR-06)
     */
    @AfterReturning(
            pointcut = "execution(* ra.edu.it211_project.service.impl.CourseServiceImpl.enrollStudent(..))",
            returning = "result"
    )
    public void logAfterEnroll(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        log.info("[AOP] @AfterReturning - Student enrolled in Course ID: {}", args[0]);
    }

    /**
     * @AfterReturning - Log sau khi upload file thành công (UC-05)
     */
    @AfterReturning(
            pointcut = "execution(* ra.edu.it211_project.service.impl.MaterialServiceImpl.uploadMaterial(..))",
            returning = "result"
    )
    public void logAfterFileUpload(JoinPoint joinPoint, Object result) {
        log.info("[AOP] @AfterReturning - Material uploaded successfully. Result: {}", result);
    }

    /**
     * @AfterThrowing - Log exception từ Service layer
     */
    @AfterThrowing(
            pointcut = "execution(* ra.edu.it211_project.service..*(..))",
            throwing = "exception"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("[AOP] @AfterThrowing - Exception in {}.{}(): {} - Message: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }

    /**
     * @Around - Đo thời gian thực thi của tất cả Service methods
     */
    @Around("execution(* ra.edu.it211_project.service..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            throw t;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.debug("[AOP] @Around - {}.{}() executed in {} ms",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    duration);
        }
        return result;
    }
}