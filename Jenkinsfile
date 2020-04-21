#!groovy
@Library('github.com/cloudogu/ces-build-lib@1.36.0')
import com.cloudogu.ces.cesbuildlib.*

node {

    properties([
            // Keep only the most recent builds in order to preserve space
            buildDiscarder(logRotator(numToKeepStr: '20')),
            // Don't run concurrent builds for a branch, because they use the same workspace directory
            disableConcurrentBuilds()
    ])

    def mvnHome = tool 'M3'
    def javaHome = tool 'JDK8'

    Maven mvn = new MavenLocal(this, mvnHome, javaHome)
    Git git = new Git(this)

    catchError {

        stage('Checkout') {
            checkout scm
            /* Don't remove folders starting in "." like
             * .m2 (maven), .npm, .cache, .local (bower)
             */
            git.clean('".*/"')
        }

        initMaven(mvn)

        stage('Build') {
            mvn 'clean install -DskipTests'
            archive '**/target/*.jar'
        }

        stage('Unit Test') {
            mvn 'test -Dmaven.test.failure.ignore=true'
            // Archive test results. Makes build unstable on failed tests.
            junit allowEmptyResults: true,
                testResults: '**/target/surefire-reports/TEST-*.xml, **/target/failsafe-reports/*.xml'
        }

        stage('Statical Code Analysis') {
            generateCoverageReportForSonarQube(mvn)

            def sonarQube = new SonarCloud(this, [sonarQubeEnv: 'sonarcloud.io', sonarOrganization: 'triologygmbh'])

            sonarQube.analyzeWith(mvn)

            if (!sonarQube.waitForQualityGateWebhookToBeCalled()) {
                unstable("Pipeline unstable due to SonarCloud quality gate failure")
            }
        }

        stage('Deploy') {
            if (preconditionsForDeploymentFulfilled()) {

                mvn.setDeploymentRepository('ossrh', 'https://oss.sonatype.org/', 'de.triology-mavenCentral-acccessToken')

                mvn.setSignatureCredentials('de.triology-mavenCentral-secretKey-asc-file',
                    'de.triology-mavenCentral-secretKey-Passphrase')

                mvn.deployToNexusRepositoryWithStaging()
            }
        }
    }

    mailIfStatusChanged(git.commitAuthorEmail)
}

boolean preconditionsForDeploymentFulfilled() {
    if (isBuildSuccessful() &&
        !isPullRequest() &&
        isBuildSuccessful() &&
        shouldBranchBeDeployed()) {
        return true
    } else {
        echo "Skipping deployment because of branch or build result: currentResult=${currentBuild.currentResult}, " +
            "result=${currentBuild.result}, branch=${env.BRANCH_NAME}."
        return false
    }
}

private boolean shouldBranchBeDeployed() {
    return env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'develop'
}

private boolean isBuildSuccessful() {
    currentBuild.currentResult == 'SUCCESS' &&
        // Build result == SUCCESS seems not to set be during pipeline execution.
        (currentBuild.result == null || currentBuild.result == 'SUCCESS')
}

void initMaven(Maven mvn) {

    if ("master".equals(env.BRANCH_NAME)) {

        echo "Building master branch"
        mvn.additionalArgs += " -DperformRelease "
        currentBuild.description = mvn.getVersion()
    }
}

static void generateCoverageReportForSonarQube(def mvn) {
    mvn 'org.jacoco:jacoco-maven-plugin:0.8.5:report'
}
