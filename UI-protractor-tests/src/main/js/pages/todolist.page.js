const format = require('util').format;

const TodoListPage = function() {

    const po = this;

    // One responsibility of a traditional Page Object
    // is to define the structure of a page the test will interact with.

    po.What_Needs_To_Be_Done  = by.id('new-todo')

    po.Items                  = by.repeater('todo in todos');

    po.Filters                = by.id('filters');

    po.Complete_Item_Checkbox = function (name) {

        // Even though the methods on a Page Object should be kept simple,
        // you'll often find that they generate dynamic locators,
        // manipulate data, try to figure out where a given page object sits in a page object hierarchy
        // and do other things that cause Page Objects to grow and quickly become difficult to maintain.

        // This method generates a dynamic locator:

        return by.xpath(format('//*[@class="view" and contains(.,"%s")]//input[@type="checkbox"]', name));
    };

    // -----------------------------------------------------------------------------------------------------------------

    // The second responsibility of a traditional Page Object
    // is to define the interactions a user can have with a specific page the Page Object models.

    po.startWithAnEmptyTodoList = function () {
        po.startWithATodoListContaining([]);
    };

    po.startWithATodoListContaining = function (items) {
        browser.get('/examples/angularjs/');
        browser.manage().window().maximize()

        items.forEach(function(item) {
            po.addATodoItemCalled(item)
        });
    };

    // Please note that every locator defined in the first section is typically resolved using a single,
    // global `browser` object. This makes it difficult to write tests
    // that require more than one browser, for example when you're testing a chat system
    // or a multi-player video game.

    po.addATodoItemCalled = function (name) {
        browser.element(po.What_Needs_To_Be_Done).sendKeys(name);
        browser.element(po.What_Needs_To_Be_Done).sendKeys(protractor.Key.ENTER);
    };

    po.addTodoItemsCalled = function (names) {
        names.forEach(function(name) {
            po.addATodoItemCalled(name)
        });
    };

    po.completeATodoItemCalled = function (name) {
        browser.element(po.Complete_Item_Checkbox(name)).click()
    };

    po.filterItemsToShowOnly = function (taskType) {
        browser.element(po.Filters).element(by.linkText(taskType)).click();
    };

    // -----------------------------------------------------------------------------------------------------------------

    // The third responsibility of a Page Object is to define the questions a user can ask about the contents
    // of the page.

    po.itemsDisplayed = function () {
        return browser.element.all(po.Items).map(function (item) {
            return item.getText();
        });
    };

    po.statusOf = function (item) {
        return browser.element(po.Complete_Item_Checkbox(item))
            .isSelected()
            .then(function(selected) {                      // Even though Protractor's API seems synchronous,
                return selected ? 'completed' : 'active';   // you'll find that you have to work with promises anyway.
            });
    };
};

module.exports = new TodoListPage();