package com.capitalone.dashboard.model;

import com.capitalone.dashboard.evaluator.ArtifactEvaluator;
import com.capitalone.dashboard.evaluator.BuildEvaluator;
import com.capitalone.dashboard.evaluator.CodeQualityEvaluator;
import com.capitalone.dashboard.evaluator.CodeReviewEvaluator;
import com.capitalone.dashboard.evaluator.Evaluator;
import com.capitalone.dashboard.evaluator.LibraryPolicyEvaluator;
import com.capitalone.dashboard.evaluator.PerformanceTestResultEvaluator;
import com.capitalone.dashboard.evaluator.RegressionTestResultEvaluator;
import com.capitalone.dashboard.evaluator.StaticSecurityAnalysisEvaluator;
import com.capitalone.dashboard.status.DashboardAuditStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DashboardAuditModel {

    //Evaluators
    private final CodeReviewEvaluator codeReviewEvaluator;
    private final BuildEvaluator buildEvaluator;
    private final CodeQualityEvaluator codeQualityEvaluator;
    private final RegressionTestResultEvaluator regressionTestResultEvaluator;
    private final PerformanceTestResultEvaluator performanceTestResultEvaluator;
    private final StaticSecurityAnalysisEvaluator staticSecurityAnalysisEvaluator;
    private final LibraryPolicyEvaluator libraryPolicyEvaluator;
    private final ArtifactEvaluator artifactEvaluator;



    @Autowired
    public DashboardAuditModel(CodeReviewEvaluator codeReviewEvaluator,
                               BuildEvaluator buildEvaluator,
                               CodeQualityEvaluator codeQualityEvaluator,
                               RegressionTestResultEvaluator regressionTestResultEvaluator,
                               PerformanceTestResultEvaluator performanceTestResultEvaluator,
                               StaticSecurityAnalysisEvaluator staticSecurityAnalysisEvaluator,
                               LibraryPolicyEvaluator libraryPolicyEvaluator,ArtifactEvaluator artifactEvaluator) {
        this.codeReviewEvaluator = codeReviewEvaluator;
        this.buildEvaluator = buildEvaluator;
        this.codeQualityEvaluator = codeQualityEvaluator;
        this.staticSecurityAnalysisEvaluator = staticSecurityAnalysisEvaluator;
        this.regressionTestResultEvaluator = regressionTestResultEvaluator;
        this.performanceTestResultEvaluator = performanceTestResultEvaluator;
        this.libraryPolicyEvaluator = libraryPolicyEvaluator;
        this.artifactEvaluator = artifactEvaluator;
    }


    public Map<AuditType, Evaluator> evaluatorMap() {
        return Collections.unmodifiableMap(Stream.of(
                new SimpleEntry<>(AuditType.CODE_REVIEW, (Evaluator)codeReviewEvaluator),
                new SimpleEntry<>(AuditType.CODE_QUALITY, (Evaluator)codeQualityEvaluator),
                new SimpleEntry<>(AuditType.STATIC_SECURITY_ANALYSIS, (Evaluator)staticSecurityAnalysisEvaluator),
                new SimpleEntry<>(AuditType.LIBRARY_POLICY, (Evaluator)libraryPolicyEvaluator),
                new SimpleEntry<>(AuditType.BUILD_REVIEW, (Evaluator)buildEvaluator),
                new SimpleEntry<>(AuditType.TEST_RESULT, (Evaluator)regressionTestResultEvaluator),
                new SimpleEntry<>(AuditType.PERF_TEST, (Evaluator)performanceTestResultEvaluator),
                new SimpleEntry<>(AuditType.ARTIFACT,(Evaluator)artifactEvaluator))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
    }


    public Map<AuditType, DashboardAuditStatus> successStatusMap() {

        return Collections.unmodifiableMap(Stream.of(
                new SimpleEntry<>(AuditType.CODE_REVIEW, DashboardAuditStatus.DASHBOARD_REPO_CONFIGURED),
                new SimpleEntry<>(AuditType.CODE_QUALITY, DashboardAuditStatus.DASHBOARD_CODEQUALITY_CONFIGURED),
                new SimpleEntry<>(AuditType.LIBRARY_POLICY, DashboardAuditStatus.DASHBOARD_LIBRARY_POLICY_ANALYSIS_CONFIGURED),
                new SimpleEntry<>(AuditType.BUILD_REVIEW, DashboardAuditStatus.DASHBOARD_BUILD_CONFIGURED),
                new SimpleEntry<>(AuditType.TEST_RESULT, DashboardAuditStatus.DASHBOARD_TEST_CONFIGURED),
                new SimpleEntry<>(AuditType.PERF_TEST, DashboardAuditStatus.DASHBOARD_PERFORMANCE_TEST_CONFIGURED),
                new SimpleEntry<>(AuditType.STATIC_SECURITY_ANALYSIS, DashboardAuditStatus.DASHBOARD_STATIC_SECURITY_ANALYSIS_CONFIGURED),
                new SimpleEntry<>(AuditType.ARTIFACT, DashboardAuditStatus.DASHBOARD_ARTIFACT_CONFIGURED))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
    }

    public Map<AuditType, DashboardAuditStatus> errorStatusMap() {

        return Collections.unmodifiableMap(Stream.of(
                new SimpleEntry<>(AuditType.CODE_REVIEW, DashboardAuditStatus.DASHBOARD_REPO_NOT_CONFIGURED),
                new SimpleEntry<>(AuditType.CODE_QUALITY, DashboardAuditStatus.DASHBOARD_CODEQUALITY_NOT_CONFIGURED),
                new SimpleEntry<>(AuditType.LIBRARY_POLICY, DashboardAuditStatus.DASHBOARD_LIBRARY_POLICY_ANALYSIS_NOT_CONFIGURED),
                new SimpleEntry<>(AuditType.BUILD_REVIEW, DashboardAuditStatus.DASHBOARD_BUILD_NOT_CONFIGURED),
                new SimpleEntry<>(AuditType.TEST_RESULT, DashboardAuditStatus.DASHBOARD_TEST_NOT_CONFIGURED),
                new SimpleEntry<>(AuditType.PERF_TEST, DashboardAuditStatus.DASHBOARD_PERFORMANCE_TEST_NOT_CONFIGURED),
                new SimpleEntry<>(AuditType.STATIC_SECURITY_ANALYSIS, DashboardAuditStatus.DASHBOARD_STATIC_SECURITY_ANALYSIS_NOT_CONFIGURED),
                new SimpleEntry<>(AuditType.ARTIFACT, DashboardAuditStatus.DASHBOARD_ARTIFACT_NOT_CONFIGURED))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
    }



}
