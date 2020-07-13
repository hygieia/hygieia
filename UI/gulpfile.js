'use strict';

// require what we need
var browserSync = require('browser-sync'),
    chalk = require('chalk'),
    gulp = require('gulp'),
    tmplCache = require('gulp-angular-templatecache'),
    change = require('gulp-change'),
    clean = require('gulp-clean'),
    concat = require('gulp-concat'),
    consolidate = require('gulp-consolidate'),
    filter = require('gulp-filter'),
    flatten = require('gulp-flatten'),
    gulpIf = require('gulp-if'),
    inject = require('gulp-inject'),
    less = require('gulp-less'),
    minifyHtml = require('gulp-htmlmin'),
    minifyCss = require('gulp-clean-css'),
    order = require('gulp-order'),
    rename = require('gulp-rename'),
    replace = require('gulp-replace'),
    httpProxy = require('http-proxy'),
    glob = require('glob'),
    runSequence = require('run-sequence'),
    wiredep = require('npm-wiredep'),
    uglify = require('gulp-uglify'),
    argv = require('yargs').argv,


    // some gulp config values
    hygieia = {
        src: 'src/',
        dist: 'dist/'
    },

    // list of where our js files come from
    jsFiles = [
        'src/{app,components,etc}/**/*.js'
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
        api: null,
        refresh: 60
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
gulp.task('build', function(callback) {
    runSequence('clean', ['assets', 'themes', 'fonts', 'js', 'views', 'test-data'], 'html', callback);
});

// run the build task, start up a browser, then
// watch the different file locations and execute
// the relevant tasks
function server(ghostMode) {
    ghostMode = typeof ghostMode == 'undefined' ? false : true
    return function () {
        /*
         * Location of your backend (API) server--default port 8080
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
            },
            ghostMode: ghostMode
        });

        gulp.watch(jsFiles).on('change', function() {
            runSequence(['js','html'], browserSync.reload);
        });

        // watch the less files in addition to the themes
        gulp.watch(themeFiles.concat(widgetStyleFiles)).on('change', function() {
            runSequence('themes', browserSync.reload);
        });

        gulp.watch(viewFiles).on('change', function() {
            runSequence('views', browserSync.reload);
        });

        gulp.watch(testDataFiles).on('change', function() {
            runSequence('test-data');
        });
    }
}

gulp.task('serve', ['build'], server());
gulp.task('serve:ghost-mode', ['build'], server(true))


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
    // get a list of widget files to import. this will only work if the theme
    // file directly has the insert:widgets code. it will not work as part of an
    // imported less file
    var widgetLessFiles = glob.sync('src/components/widgets/**/*.less', null);
    widgetLessFiles = widgetLessFiles.map(function(file) {
        return "@import '" + file.replace(hygieia.src, '../../') + "';";
    });

    return gulp.src(themeFiles)
        .pipe(replace('/** insert:widgets **/', widgetLessFiles.join('')))
        .pipe(less({
            paths: [
                hygieia.src + 'components'
            ]
        }))
        .on('error', function(err) {
            console.error(err.toString());
            this.emit('end');
        })
        .pipe(minifyCss())
        .pipe(gulp.dest(hygieia.dist + 'styles'));
});

// move js files over
gulp.task('js', function() {
    var stream = gulp.src(jsFiles);

    if(!!argv.prod) {
        stream = stream.pipe(concat('app/app.js'));
    }

    return stream.pipe(gulp.dest(hygieia.dist));
});

// move our html views to a template cache folder so each one
// doesn't need to be happen as an ajax request
gulp.task('views', function() {
    return gulp
        .src(viewFiles)
        .pipe(minifyHtml({
            collapseWhitespace: true
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
            'node_modules/components-font-awesome/**/*',
        ])
        .pipe(filter('**/*.{eot,ttf,woff,woff2}'))
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
        .pipe(inject(gulp.src(
            wiredep({
                directory: 'node_modules',
                exclude: [/bootstrap\.js/, /bootstrap\.css/, /bootstrap\.css/, /foundation\.css/, /bin\.js/, /strip-json-comments\/cli\.js/]
            }).js)
                .pipe(gulp.dest(hygieia.dist + 'node_modules')),
            { name: 'bower', ignorePath: hygieia.dist, addRootSlash: false })
        )
        .pipe(gulp.dest(hygieia.dist))

        // replace inject:js with script references to all the files in the following sources
        .pipe(inject(gulp.src(
            !!argv.prod ? ['src/app/app.js'] : jsFiles)
                .pipe(order(['app/app.js', 'app/dashboard/core/module.js', 'app/**/*.js', 'components/**/*.js','etc/**/*.js','etc/gridstack.min.map'])),
            { name: 'hygieia', ignorePath: 'src', addRootSlash: false }))

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


gulp.task('chartist', function() {
    return gulp
        .src(['src/app/chartist/chartist.less'])
        .pipe(less())
        .pipe(gulp.dest('./'));
});
