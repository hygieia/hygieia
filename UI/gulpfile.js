'use strict';

// require what we need
var browserSync = require('browser-sync'),
    chalk = require('chalk'),
    gulp = require('gulp'),
    tmplCache = require('gulp-angular-templatecache'),
    clean = require('gulp-clean'),
    consolidate = require('gulp-consolidate'),
    filter = require('gulp-filter'),
    flatten = require('gulp-flatten'),
    gulpIf = require('gulp-if'),
    inject = require('gulp-inject'),
    less = require('gulp-less'),
    minifyHtml = require('gulp-minify-html'),
    replace = require('gulp-replace'),
    httpProxy = require('http-proxy'),
    runSequence = require('run-sequence'),
    wiredep = require('wiredep'),
    argv = require('yargs').argv,


    // some gulp config values
    hygieia = {
        src: 'src/',
        dist: 'dist/'
    },

    // list of where our js files come from
    jsFiles = [
        'src/{app,components}/**/*.js'
    ],

    // list of theme files for less processing
    themeFiles = [
        'src/components/themes/*.less'
    ],

    // look for html files
    viewFiles = [
        'src/{app,components}/**/*.html'
    ],

    widgetStyleFiles = [
        'src/{app,components}/**/*.less'
    ],

    // look for local json files
    testDataFiles = [
        'src/test-data/*'
    ],

    // config values that will be written to the UI and can
    // be overwritten by arguments during the build process.
    // if left null, they will not be written to the file
    config = {
        module: 'hygieia-dashboard',
        local: null,
        api: null
    };

// override config values
for(var field in config) {
    var val = argv[field];
    if(val) {
        if(val == 'true' || val == 'false') {
            val = val !== 'false';
        }
        config[field] = val;
    }

    if(config[field] === null) {
        delete config[field];
    }
}

/*******************************
 * MAIN TASKS
 *******************************/
gulp.task('default', ['build']);

// moves everything to the build folder
gulp.task('build', function() {
    runSequence('clean', 'assets', 'themes', 'fonts', 'js', 'views', 'test-data', 'html');
});

// run the build task, start up a browser, then
// watch the different file locations and execute
// the relevant tasks
gulp.task('serve', function() {
    runSequence('build', function() {
        /*
         * Location of your backend server
         */
        var proxyTarget = config.api || 'http://localhost:8080';

        var proxy = httpProxy.createProxyServer({
            target: proxyTarget
        });

        proxy.on('error', function(error, req, res) {
            res.writeHead(500, {
                'Content-Type': 'text/plain'
            });

            console.error(chalk.red('[Proxy]'), error);
        });

        /*
         * The proxy middleware is an Express middleware added to BrowserSync to
         * handle backend request and proxy them to your backend.
         */
        function proxyMiddleware(req, res, next) {
            /*
             * Proxy the REST API.
             */
            if (/^\/api\/.*/.test(req.url)) {
                proxy.web(req, res);
            } else {
                next();
            }
        }

        browserSync.init({
            server: {
                baseDir: hygieia.dist,
                startPath: '/',
                middleware: [proxyMiddleware]
            }
        });

        gulp.watch(jsFiles).on('change', function() {
            runSequence('js', browserSync.reload);
        });

        // watch the less files in addition to the themes
        gulp.watch(themeFiles.concat(widgetStyleFiles)).on('change', function() {
            runSequence('themes', browserSync.reload);
        });

        gulp.watch(viewFiles).on('change', function() {
            runSequence('views', browserSync.reload);
        });

        gulp.watch(testDataFiles).on('change', function() {
            runSequence('test-data', browserSync.reload);
        });
    });
});



/*******************************
 * SUPPORTING TASKS
 *******************************/
// delete our distribution folder
gulp.task('clean', function() {
    return gulp.src(hygieia.dist).pipe(clean());
});

// move everything in the assets folder to distribution
gulp.task('assets', function() {
    return gulp
        .src([
            hygieia.src + 'assets/**/*'
        ])
        .pipe(gulp.dest(hygieia.dist + 'assets'));
});

// loop through and grab all the theme less files,
// process them, and place them in the styles folder.
// the app adds the references to the stylesheet based
// on user preferences so there is no need to inject the
// files in to the UI directly
gulp.task('themes', function() {
    gulp.src(['src/app/css/widgets.less'])
        .pipe(inject(gulp.src(['src/components/widgets/**/*.less']), {
            starttag: '/* inject:imports */',
            endtag: '/* endinject */',
            transform: function (filepath) {
                return '@import "' + filepath.replace('/src', '../..') + '";';
            }
        }))
        .pipe(gulp.dest('src/app/css'));

    return gulp.src(themeFiles)
        .pipe(less({
            paths: [
                hygieia.src + 'components'
            ]
        }))
        .pipe(gulp.dest(hygieia.dist + 'styles'));
});

// move js files over
gulp.task('js', function() {
    return gulp
        .src(jsFiles)
        .pipe(gulp.dest(hygieia.dist));
});

// move our html views to a template cache folder so each one
// doesn't need to be happen as an ajax request
gulp.task('views', function() {
    return gulp
        .src(viewFiles)
        .pipe(minifyHtml({
            empty: true,
            spare: true,
            quotes: true
        }))
        .pipe(tmplCache('template-cache.js', {
            module: config.module
        }))
        .pipe(gulp.dest(hygieia.dist));
});

// move our fonts folder
gulp.task('fonts', function() {
    return gulp
        .src([
            'bower_components/**/*'
        ])
        .pipe(filter('**/*.{eot,ttf,woff}'))
        .pipe(flatten())
        .pipe(gulp.dest(hygieia.dist + 'fonts'));
});

// move the source html files and inject the widget javascript
gulp.task('html', function() {
    return gulp
        // move the root html files to the distribution folder
        .src([
            hygieia.src + '*.html',
            hygieia.src + 'favicon.ico'
        ])
        .pipe(gulp.dest(hygieia.dist))

        // now just work with the main index file
        .pipe(filter(['index.html']))

        // wiredep replaces bower:js with references to all bower dependencies
        .pipe(inject(gulp.src(wiredep({exclude: [/bootstrap\.js/, /bootstrap\.css/, /bootstrap\.css/, /foundation\.css/]}).js).pipe(gulp.dest(hygieia.dist + 'bower_components')), { name: 'bower', ignorePath: hygieia.dist, addRootSlash: false }))
        .pipe(gulp.dest(hygieia.dist))

        // replace inject:js with script references to all the files in the following sources
        .pipe(inject(gulp.src(jsFiles), { name: 'hygieia', ignorePath: 'src', addRootSlash: false }))

        // replace custom placeholders with our configured values
        .pipe(replace('[config]', JSON.stringify(config)))
        .pipe(replace('[config-module]', config.module))

        // make sure the file is rewritten
        .pipe(gulp.dest(hygieia.dist));
});

// move test data files, but only if running locally
gulp.task('test-data', function() {
    return gulp
        .src(testDataFiles)
        .pipe(gulpIf(config.local, gulp.dest(hygieia.dist + 'test-data')));
});

