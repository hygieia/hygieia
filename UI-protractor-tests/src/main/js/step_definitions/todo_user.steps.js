const
    todoListPage = require('../pages/todolist.page'),
    listOf = require('../text').listOf,
    expect = require('../expect').expect;

module.exports = function todoUserSteps() {

    this.setDefaultTimeout(60 * 1000);

    this.Given(/^.*that I have an empty todo list$/, () => {
        todoListPage.startWithAnEmptyTodoList();
    });

    this.Given(/^.*that I have a todo list containing (.*)$/, (items) => {
        todoListPage.startWithATodoListContaining(listOf(items));
    });

    this.When(/^I add (.*?) to my list$/, (item) => {
        todoListPage.addATodoItemCalled(item);
    });

    this.When(/^I complete (.*)$/, (item) => {
        todoListPage.completeATodoItemCalled(item);
    });

    this.When(/^I filter my list to show (?:only )?(.*) tasks$/, (taskType) => {
        todoListPage.filterItemsToShowOnly(taskType);
    });

    this.Then(/^(.*?) should be recorded in my list$/, (item) => {
        expect(todoListPage.itemsDisplayed()).to.eventually.contain(item);
    });

    this.Then(/^(.*?) should be marked as (.*?)$/, (item, status) => {
        expect(todoListPage.statusOf(item)).to.eventually.equal(status);
    });

    this.Then(/^.* todo list should contain (.*?)$/, (items) => {
        expect(todoListPage.itemsDisplayed()).to.eventually.eql(listOf(items));
    });
};