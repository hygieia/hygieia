'use strict';

var consolidate = require('gulp-consolidate');
var rename = require('gulp-rename');
var gulp = require('gulp');

var engines = [
  ['jade',{'pretty':'  '}]
];

function buildTemplates(engine, src, dest) {
  return gulp.src(src)
    .pipe(consolidate.apply(this, engine))
    .pipe(rename(function (path) {path.extname = '.html';}))
    .pipe(gulp.dest(dest));
}

function buildTaskFunction(engine) {
  return function() {
    buildTemplates(engine, 'src/app/**/*.jade', '.tmp/app/');
    buildTemplates(engine, 'src/components/**/*.jade', '.tmp/components/');
  };
}

var tasks = [];

for (var i=0, l=engines.length; i < l; i++) {
  var engine = engines[i];

  gulp.task('consolidate:' + engine[0] + ':app', buildTemplates.bind(this, engine, 'src/app/**/*.jade', '.tmp/app/'));
  gulp.task('consolidate:' + engine[0] + ':components', buildTemplates.bind(this, engine, 'src/components/**/*.jade', '.tmp/components/'));
  gulp.task('consolidate:' + engine[0], ['consolidate:' + engine[0] + ':app', 'consolidate:' + engine[0] + ':components' ]);

  tasks.push('consolidate:' + engine[0]);
}

gulp.task('consolidate', tasks);
