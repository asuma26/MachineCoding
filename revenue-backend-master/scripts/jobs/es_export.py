from elasticsearch import Elasticsearch

client = Elasticsearch(['http://logs.wynk.in:80'])

# valid client instance for Elasticsearch
if client != None:
    index = 'wcf-payments-2*'
    # declare a filter query dict object
    match_all = {
        "size": 100,
        "query": {
            "match_all": {}
        }
    }
with open("payments.log", 'a') as file:
    # make a search() request to get all docs in the index
    resp = client.search(
        index=index,
        body=match_all,
        scroll='2s'  # length of time to keep search context
    )

    # keep track of pass scroll _id
    old_scroll_id = resp['_scroll_id']

    # use a 'while' iterator to loop over document 'hits'
    while len(resp['hits']['hits']):

        # make a request using the Scroll API
        resp = client.scroll(
            scroll_id=old_scroll_id,
            scroll='2s'  # length of time to keep search context
        )

        # check if there's a new scroll ID
        if old_scroll_id != resp['_scroll_id']:
            print("NEW SCROLL ID:", resp['_scroll_id'])

        # keep track of pass scroll _id
        old_scroll_id = resp['_scroll_id']

        # print the response results
        print("\nresponse for index:", index)
        print("_scroll_id:", resp['_scroll_id'])
        print('response["hits"]["total"]["value"]:', resp["hits"]["total"]["value"])

        # iterate over the document hits for each 'scroll'
        for doc in resp['hits']['hits']:
            file.write(doc['_id'], doc['_source'])
