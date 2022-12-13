import json
import os
import requests
from multiprocessing import Process

def lambda_handler(event, context):
    
    url = os.environ['WEBSITE_URL']
    count = int(os.environ['TRIGGER_COUNT'])
    i = 0
    while i <= count:
        i = i + 1
        makeHTTPRequest(url, i)
    
    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }

def makeHTTPRequest(url, i):
    print(str(i) + " Making call to: " + url)
    requests.get("https://lerneprogrammieren.de")