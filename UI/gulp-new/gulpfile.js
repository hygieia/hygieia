// require what we need
var browserSync = require('browser-sync'),
    del = require('del'),
    gulp = require('gulp'),
    tmplCache = require('gulp-angular-templatecache'),
    consolidate = require('gulp-consolidate'),
    filter = require('gulp-filter'),
    flatten = require('gulp-flatten'),
    gulpIf = require('gulp-if'),
    inject = require('gulp-inject'),
    less = require('gulp-less'),
    minifyHtml = require('gulp-minify-html'),
    replace = require('gulp-replace'),
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
        'src/{app,components}/**/*.js',
        '!src/app/config.js',
        '!src/app/local-testing.js',
        '!src/{app,components}/**/*.spec.js',
        '!src/{app,components}/**/*.mock.js'
    ],

    themeFiles = [
        'src/components/themes/*.less'
    ],

    cssFiles = themeFiles.concat([
        'src/{app,components}/**/*.less'
    ]),

    viewFiles = [
        'src/{app,components}/**/*.html',
        '.tmp/{app,components}/**/*.html'
    ],

    testDataFiles = [
        'src/test-data/*'
    ],

    // config values that will be written to the UI and can
    // be overwritten by arguments during the build process
    config = {
        module: 'hygieia-dashboard',
        local: false,
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
}

/*******************************
 * MAIN TASKS
 *******************************/
// moves everything to the build folder
gulp.task('build', function(cb) {
    runSequence('clean', 'themes', 'assets', 'fonts', 'js', 'views', 'test-data', 'html', cb);
});

// run the build task, start up a browser, then
// watch the different file locations and execute
// the relevant tasks
gulp.task('serve', function() {
    runSequence('build', function() {
        browserSync.init({
            server: {
                baseDir: hygieia.dist,
                startPath: '/'
            }
        });

        gulp.watch(jsFiles).on('change', function() {
            runSequence('js', browserSync.reload);
        });

        gulp.watch(cssFiles).on('change', function() {
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
    return del([
        hygieia.dist
    ]);
});

// move everything in the assets folder to distribution
gulp.task('assets', function() {
    return gulp
        .src(hygieia.src + 'assets/**/*')
        .pipe(gulp.dest(hygieia.dist + 'assets'));
});

// loop through and grab all the theme less files,
// process them, and place them in the styles folder.
// the app adds the references to the stylesheet based
// on user preferences so there is no need to inject the
// files in to the UI directly
gulp.task('themes', function() {
    return gulp
        .src(themeFiles.concat([

        ]))
        .pipe(less({
            paths: [
                hygieia.src + '/components'
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
        .src(hygieia.src + '*.html')
        .pipe(gulp.dest(hygieia.dist))
        .pipe(filter(['index.html']))

        // wiredep replaces bower:js with references to all bower dependencies
        .pipe(inject(gulp.src(wiredep({exclude: [/bootstrap\.js/, /bootstrap\.css/, /bootstrap\.css/, /foundation\.css/]}).js).pipe(gulp.dest(hygieia.dist + 'bower_components')), { name: 'bower', ignorePath: hygieia.dist, addRootSlash: false }))
        .pipe(gulp.dest(hygieia.dist))

        // replace inject:js with script references to all the files in the following sources
        .pipe(inject(gulp.src(jsFiles), { name: 'hygieia', ignorePath: 'src', addRootSlash: false }))

        .pipe(replace('[config]', JSON.stringify(config)))
        .pipe(replace('[config-module]', config.module))
        .pipe(gulp.dest(hygieia.dist));
});

gulp.task('test-data', function() {
    return gulp
        .src(testDataFiles)
        .pipe(gulpIf(config.local, gulp.dest(hygieia.dist + 'test-data')));
});

