---
title: Create and Edit the Documentation
tags:
keywords:
toc: true
summary: Brief instructions to help you create new content or edit the existing documentation for Hygieia.
sidebar: hygieia_sidebar
permalink: documentation.html
---

To create and edit markdown files for Hygieia documentation, follow the instructions given below:


*	**Step 1: Clone the gh-pages branch from your fork**

    To make updates to documentation locally, clone the gh-pages branch from your fork:
	
	```bash
	git clone -b gh-pages <https://github.com/<your-repo>/Hygieia.git>
	```
	
*	**Step 2: Create and Edit Markdown files**
    
    - *Location Details*
	
	  All markdown files are saved in the **Pages** folder in the `gh-pages` branch. Create and edit all markdown files in this folder.
	
	  All screenshots are saved at `media/images/`. You can add and save all screenshots to this folder.
	
	- *Frontmatter for Pages*
	
	  Make sure each page has frontmatter at the top as follows:
	  
	  ```properties
	  ---
      title: Create and Edit the Documentation
      toc: true
      summary: Contribute to Hygieia Documentation to creating new content or editing the existing documentation.
      sidebar: hygieia_sidebar
      permalink: documentation.html
      ---
	  ```
	  
	For a tutorial on GitHub-flavored markdown, see the [GitHub Guide]( https://guides.github.com/features/mastering-markdown/).

*	**Step 3: Make Changes to the Table of Contents**	
	
    To link new files to the table of contents, edit the YAML files available in the `/_data/sidebars` folder in `gh-pages` branch. 
	
    To make changes to the top-navigation in the web pages, edit the `topnav.yml` file in the `/_data` folder.

*	**Step 4: Commit Changes**	
    
	Once you finish making changes to the markdown files, commit your changes.
	In the command line or terminal, execute the following steps:
	
	```bash
    git add <your file name>
    git commit -m “Initial commit”
    git push origin gh-pages
	```
	
*	**Step 5: Create a Pull Request**	

    To propose your changes to Hygieia documentation, create and submit a pull request. For information on Pull Requests, see the [GitHub documentation]( https://help.github.com/articles/creating-a-pull-request/#creating-the-pull-request).

**Note**: For information about this theme and to build the site locally, follow the instructions [here](http://idratherbewriting.com/documentation-theme-jekyll/index.html).