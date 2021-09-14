Scrape API:
Request:
    
    ScrapeResponse status = driver.findById(id);

Response:

    {
        "id": "61406141572e885322d4b7b3",
        "status": "Processing",
        "pageStatus": "Created",
        "isDone": false,
        "estimatedWaitTime": 20,
        "pageContentBytes": 0,
        "statusCode": 102,
        "pageStatusCode": 201,
        "timestamp": "2021-09-14T08:45:53.743921Z"
    }

Fields explained:

    id: The scrape id, last return by driver.submit(sql, true);
    status: The scrape status
    statusCode: The scrape status code
    pageStatus: The page status of this scrape
    pageStatusCode: The page status code of this scrape
    isDone: The scrape is done or not
    estimatedWaitTime: The estimated time in seconds to finish this task
    timestamp: The timestamp of this response

Dashboard API:
Request: 

    Dashboard dashboard = driver.dashboard();

Response:

    {
        "authToken": "...",
        "timestamp": "2021-09-14T16:45:53.837953+08:00",
        "profile": {
            "balance": 0.0,
            "visitCounter": {
                "visitsLastMinute": 10,
                "visitsLastTenMinutes": 10,
                "visitsLastHour": 10,
                "visitsLastDay": 10,
                "maxVisitsPMinute": 60,
                "maxVisitsPTenMinutes": 200,
                "maxVisitsPHour": 300,
                "maxVisitsPDay": 1000
            }
        },
        "monthlySummary": {},
        "dailySummary": {},
        "hourlySummary": {}
    }

Fields explained:

    profile.visitCounter: The visit counters and limits
