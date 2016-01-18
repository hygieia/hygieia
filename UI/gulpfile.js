'use strict';

var gulp = require('gulp');

//require('require-dir')('./gulp');
require('require-dir')('./gulp-new');

gulp.task('default', function () {
    gulp.start('build');
});
