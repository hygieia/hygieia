function karateConfig() {
    var env = karate.env; // get system property 'karate.env'
    karate.log('karate.env system property was:', env);
    if (!env) {
        env = 'dev';
    }
    var config = {
        env: env,
        baseUrl: 'http://localhost:8888/api-audit/'
    }

    if (env == 'dev') {
        config.baseUrl = 'http://localhost:8888/api-audit/';

    }

    else if (env == 'qa') {
        config.baseUrl = 'https://my-qa-environment.com/apiaudit/';
    }

    else if (env == 'prod') {
        config.baseUrl = 'https://my-prod-environment.com/apiaudit/';
    }

    return config;
}