'use strict';

var gulp = require('gulp');
var less = require('gulp-less');

gulp.task('watch', ['consolidate', 'wiredep', 'injector:css', 'themes-watcher', 'injector:js', 'fonts'] ,function () {
  gulp.watch('src/{app,components}/**/*.less', ['injector:css', 'themes-watcher']);
  gulp.watch('src/{app,components}/**/*.js', ['injector:js']);
  gulp.watch('src/assets/images/**/*', ['images']);
  gulp.watch('src/assets/fonts/**/*', ['fonts']);
  gulp.watch('bower.json', ['wiredep']);
  gulp.watch('src/{app,components}/**/*.jade', ['consolidate:jade']);
});

gulp.task('themes-watcher', function() {
    return gulp.src('src/components/themes/*.less')
        .pipe(less({
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
        .pipe(gulp.dest('.tmp/styles/'));
});
