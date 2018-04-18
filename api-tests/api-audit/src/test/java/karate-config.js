function karateConfig() {
    var env = karate.env; // get system property 'karate.env'
    karate.log('karate.env system property was:', env);
    if (!env) {
        env = 'qa';
    }
    var config = {
        env: env,
        baseUrl: 'https://hygieia-qa.cloud.capitalone.com/apiaudit/'
    }

    if (env == 'dev1') {
        config.baseUrl = 'https://hygieia-qa.cloud.capitalone.com/api/';

    }

    else if (env == 'qa') {
        config.baseUrl = 'https://hygieia-qa.cloud.capitalone.com/apiaudit/';
    }

    else if (env == 'prod') {
        config.baseUrl = 'https://hygieia.cloud.capitalone.com/api/';
    }

    return config;
}