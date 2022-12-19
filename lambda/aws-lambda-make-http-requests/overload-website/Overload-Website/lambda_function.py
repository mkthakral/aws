import json
import os
import requests
from multiprocessing import Process

def lambda_handler(event, context):
    paramSolution = event["queryStringParameters"]["solution"]
    paramTriggerCount = event["queryStringParameters"]["trigger"]

    url = ''

    if paramSolution == "1": 
        url = os.environ['SOLUTION_1_WEBSITE_URL']
    else:
        url = os.environ['SOLUTION_2_WEBSITE_URL']
    

    count = int(paramTriggerCount)
    i = 0
    while i < count:
        i = i + 1
        makeHTTPRequest(url, i)
    
    return {
        'statusCode': 200,
        'body': json.dumps('Complete!')
    }

def makeHTTPRequest(url, i):
    print(str(i) + " Making call to: " + url)
    requests.get("https://lerneprogrammieren.de")