'use strict';

var gulp = require('gulp');

var $ = require('gulp-load-plugins')({
    pattern: ['gulp-*', 'main-bower-files', 'uglify-save-license', 'del',
              'minimist']
});

gulp.task('themes', ['wiredep'], function() {
    return gulp.src('src/components/themes/*.less')
        .pipe($.less({
            paths: [
                'src/bower_components',
                'src/app',
                'src/components'
            ]
        }))
        .on('error', function handleError(err) {
            console.error(err.toString());
            this.emit('end');
        })
        .pipe(gulp.dest('dist/styles'));
});

gulp.task('styles', ['wiredep', 'injector:css:preprocessor'], function () {
    return gulp.src(['src/app/index.less'])
        .pipe($.less({
            paths: [
                'src/bower_components',
                'src/app',
                'src/components'
            ]
        }))
        .on('error', function handleError(err) {
            console.error(err.toString());
            this.emit('end');
        })
        .pipe($.autoprefixer())
        .pipe(gulp.dest('.tmp/app/'));
});

gulp.task('injector:css:preprocessor', function () {
    return gulp.src('src/app/index.less')
        .pipe($.inject(gulp.src([
            'src/{app,components}/**/*.less',
            '!src/components/themes/*.less',
            '!src/app/css/*.less',
            '!src/app/index.less',
            '!src/app/vendor.less',
            '!src/app/chartist/*.less'
        ], {read: false}), {
            transform: function (filePath) {
                filePath = filePath.replace('src/app/', '');
                filePath = filePath.replace('src/components/', '../components/');
                return '@import \'' + filePath + '\';';
            },
            starttag: '// injector',
            endtag: '// endinjector',
            addRootSlash: false
        }))
        .pipe(gulp.dest('src/app/'));
});

gulp.task('injector:css', ['styles'], function () {
    return gulp.src('src/index.html')
        .pipe($.inject(gulp.src([
            '.tmp/{app,components}/**/*.css',
            '!.tmp/app/vendor.css'
        ], {read: false}), {
            ignorePath: '.tmp',
            addRootSlash: false
        }))
        .pipe(gulp.dest('src/'));
});

gulp.task('scripts', function () {
    return gulp.src('src/{app,components}/**/*.js')
        .pipe($.jshint())
        .pipe($.jshint.reporter('jshint-stylish'));
});

gulp.task('local-testing', function() {
  var localOption = {
    boolean: 'local',
    default: { local: false }
  };

  var options = $.minimist(process.argv.slice(2), localOption);

  return gulp.src(['src/app/local-testing.js'])
    .pipe($.replace(/localTesting.*=.*/, 'localTesting = '+ options.local.toString() + ';'))
    .pipe(gulp.dest('src/app'));
})

gulp.task('injector:js', ['local-testing', 'scripts', 'injector:css'], function () {
    return gulp.src(['src/index.html', '.tmp/index.html'])
        .pipe($.inject(gulp.src([
            'src/{app,components}/**/*.js',
            '!src/app/local-testing.js',
            '!src/{app,components}/**/*.spec.js',
            '!src/{app,components}/**/*.mock.js'
        ]).pipe($.angularFilesort()), {
            ignorePath: 'src',
            addRootSlash: false
        }))
        .pipe(gulp.dest('src/'));
});


gulp.task('partials', ['consolidate'], function () {
    return gulp.src(['src/{app,components}/**/*.html', '.tmp/{app,components}/**/*.html'])
        .pipe($.minifyHtml({
            empty: true,
            spare: true,
            quotes: true
        }))
        .pipe($.angularTemplatecache('templateCacheHtml.js', {
            module: 'devops-dashboard'
        }))
        .pipe(gulp.dest('.tmp/inject/'));
});

gulp.task('html', ['wiredep', 'injector:css', 'injector:js', 'partials'], function () {
    var htmlFilter = $.filter('*.html');
    var jsFilter = $.filter('**/*.js');
    var cssFilter = $.filter('**/*.css');
    var assets;

    return gulp.src(['src/*.html', '.tmp/*.html'])
        .pipe($.inject(gulp.src('.tmp/inject/templateCacheHtml.js', {read: false}), {
            starttag: '<!-- inject:partials -->',
            ignorePath: '.tmp',
            addRootSlash: false
        }))
        .pipe(assets = $.useref.assets())
        .pipe($.rev())
        .pipe(jsFilter)
        .pipe($.ngAnnotate())
        .pipe($.uglify({preserveComments: $.uglifySaveLicense}))
        .pipe(jsFilter.restore())
        .pipe(cssFilter)
        .pipe($.replace('bower_components/bootstrap/fonts', 'fonts'))
        .pipe($.csso())
        .pipe(cssFilter.restore())
        .pipe(assets.restore())
        .pipe($.useref())
        .pipe($.revReplace())
        .pipe(htmlFilter)
        .pipe($.minifyHtml({
            empty: true,
            spare: true,
            quotes: true
        }))
        .pipe(htmlFilter.restore())
        .pipe(gulp.dest('dist/'))
        .pipe($.size({title: 'dist/', showFiles: true}));
});

gulp.task('images', function () {
    return gulp.src('src/assets/images/*')
        .pipe(gulp.dest('dist/assets/images/'))
        .pipe(gulp.dest('.tmp/assets/images/'));
});

gulp.task('fonts', function () {
    var paths = $.mainBowerFiles();
    paths.push('src/assets/fonts/**/*');
    return gulp.src(paths)
        .pipe($.filter('**/*.{eot,svg,ttf,woff}'))
        .pipe($.flatten())
        .pipe(gulp.dest('dist/fonts/'))
        .pipe(gulp.dest('.tmp/fonts'));
});

gulp.task('misc', function () {
    return gulp.src('src/**/*.ico')
        .pipe(gulp.dest('dist/'));
});

gulp.task('clean', function (done) {
    $.del(['dist/', '.tmp/'], done);
});

gulp.task('build', ['html', 'themes', 'images', 'fonts', 'misc']);
