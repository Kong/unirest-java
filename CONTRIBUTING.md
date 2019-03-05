# Contributing to Unirest

:+1::tada: First off, thanks for taking the time to contribute! :tada::+1:

## Code of Conduct

This project and everyone participating in it is governed by the [Open Unirest Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs
**Explain how to produce the problem** in as many details as possible. When listing steps, **don't just say what you did, but explain how you did it**. 
* **Provide specific examples to demonstrate the steps**. Include links to files or GitHub projects, or copy/pasteable snippets, which you use in those examples. If you're providing snippets in the issue, use [Markdown code blocks](https://help.github.com/articles/markdown-basics/#multiple-lines).
* **Describe the behavior you observed after following the steps** and point out what exactly is the problem with that behavior.
* **Explain which behavior you expected to see instead and why.**

Include details about your configuration and environment:

* **What version of Unirest were you using?**
* **What version of Java were you running it on?**
* **Did you do any custom configuration to the client?**


### Contributing Code

* Before you put in all the work to add a new feature. Try opening a issue and checking if it's something that will be accepted. Sometimes a feature you think might be useful might be contrary to the goals or direction of the project. We won't want folks to get frustrated, so take a minute and talk it through with us.
* If we do all agree it's a good idea we would expect:
   * Backwards compatibility is important to this project and you should go out of your way to not make breaking changes.
   * All contributions should be issued as pull requests and get reviewed by the admins of this project.
   * All code should follow style guidelines enforced by the checkstyle and other static analysis.
   * All code should have tests.
      * Unit tests
      * Behavioral tests (see https://github.com/Kong/unirest-java/tree/master/src/test/java/BehaviorTests)
  * You should have run verify before submitting and the PR needs to have passed TravisCI before being merged.
