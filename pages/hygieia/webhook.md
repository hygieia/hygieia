---
title: GitHub Webhook
tags:
keywords: 
toc: true
summary: 
sidebar: hygieia_sidebar
permalink: webhook.html
---

You can use GitHub webhooks to publish commit information to the Feature widget in the Hygieia dashboard. If you use webhooks, you need not run the GitHub collector.

Your Github webhook’s payload URL should be set to: `http://hygieia-base-url/api/commit/github/v3`.

Select the option of publishing just the **push** events.