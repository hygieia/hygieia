# Hygieia Contribution Guidelines

Thank you for your interest in contributing to Hygieia! It is because of developers like you that this project is able to succeed.

In order to ensure the highest standards of security and code quality, we have laid out some guidelines that should be followed when contributing to Hygieia.

## General Pull Request (PR) Guidelines

* PR's should fix one issue or add/update one feature. If you would like to update multiple things, please separate into individual PRs so that they can be adequately reviewed.
* PR titles should be succint and descriptive. The "description" field should specify which issue/feature the PR relates to, give an overview of changes made, and highlight any specific lines or files that may need extra review.

## Testing and Linting
* Please run ```ng lint``` and address any errors before requesting review.
* We use [Karma](https://karma-runner.github.io/latest/index.html) for testing. Before opening your PR, please add unit tests for any new features and update any existing ones necessary for the test suite to pass.
* Testing thresholds must be met as follows:
  * Branch Coverage: 80%
  * Line Coverage: 80%
  * Statement Coverage: 80%
  * Function Coverage: 80%
  *  ** As of now, the existing codebase does not meet these standards. We are working on getting to this level of coverage, and you can do you part by making sure any new code has at least 80% coverage **

## To raise an issue/ report a bug
* Please use github issues and include a detailed description, including screenshots if applicable.

## Contact us
* The Hygieia development team is happy to assist with any issues related to contributing to this project. Please contact us at ** INSERT EMAIL HERE **


Thanks again for your help, and we look forward to seeing the great new features you contribute to the Hygieia ecosystem!

