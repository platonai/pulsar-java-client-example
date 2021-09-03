Pulsar client example in java

Submit an X-SQL to scrape:

    String sql = "...";
    String id = driver.submit(sql, true);

Check the status of a specified scrape task:

    ScrapeResponse status = driver.findById(id);

Check our dashboard:

    Dashboard dashboard = driver.dashboard();

Download all scrape results page by page:

    Page<CompactedScrapeResponse> results = driver.download(0, 10);

How to write an X-SQL to scrape a webpage?

Most of the X-SQLs look like this:

    select
        dom_first_text(dom, '[[a-css-selector-to-locate-a-field]]') as fieldName
    from
        load_and_select('{{url}}', '[[a-restrict-css-selector-to-locate-the-dom]]');
    
Click [here](src/main/resources/sites/amazon/crawl) for examples.
