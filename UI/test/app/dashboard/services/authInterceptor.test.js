// Unit Testing for AuthInterceptor
describe("AuthInterceptor Unit Tests", function() {
    var httpProvider;
    var http;
    var httpBackend;
    var authInterceptor;
    var authService;
    var tokenService;
    var userService;
    var token = 'someToken';

    beforeEach(function() {
        module(HygieiaConfig.module, function ($httpProvider) {
            httpProvider = $httpProvider;
        });

        inject(function(_tokenService_, _userService_, _authInterceptor_, _authService_, $http, $httpBackend) {
            tokenService = _tokenService_;
            userService = _userService_;
            authInterceptor = _authInterceptor_;
            authService = _authService_;
            http = $http;
            httpBackend = $httpBackend;
        });
    });

    describe("AuthInterceptor Tests", function() {
        it('should have AuthInterceptor as an interceptor', function () {
            expect(httpProvider.interceptors).toContain('authInterceptor');
        });

        it('should be defined', function () {
            expect(authInterceptor).toBeDefined();
        });

        it('should properly add an Authentication header to an http config', function () {
            // reset token to null
            tokenService.setToken(null);

            tokenService.setToken(token);
            var config = httpProvider.defaults;
            authInterceptor['request'](config);

            expect(config.headers.Authorization).toBeDefined();
            expect(config.headers.Authorization).toBe(token);
        });

        // E2E mock test
        it('should add the saved token from api to the header of all outgoing requests', function () {
            // reset token to null
            tokenService.setToken(null);
            // initial access
            expect(tokenService.getToken()).toBe('null');

            // user register/login
            http.post('/route', null, httpProvider.defaults)
                .then(function(response) {
                    expect(response.config.headers['X-Authentication-Token']).toBe(token);
                    tokenService.setToken(response.config.headers['X-Authentication-Token']);
                    expect(tokenService.getToken()).toBe(token);
                }).catch(function(error) {
                expect(response.status).toBe(200);
            });

            // mock backend server response
            httpBackend.when('POST', '/route')
                .respond(function(method, url, data, headers) {
                    headers['X-Authentication-Token'] = token;
                    return [200, data, headers];
                });

            // flush pending requests
            httpBackend.flush();

            // a follow-up request
            http.get('/anotherRoute', null, httpProvider.defaults)
                .then(function(response) {
                    expect(response.status).toBe(200);
                }).catch(function(error) {
                expect(response.status).toBe(200);
            });

            // mock backend server response
            httpBackend.when('GET', '/anotherRoute')
                .respond(function(method, url, data, headers) {
                    expect(headers['Authorization']).toBeDefined();
                    expect(headers['Authorization']).toBe(token);
                    return [200];
                });

            httpBackend.flush();

        });
    }); // AuthInterceptor Tests
});