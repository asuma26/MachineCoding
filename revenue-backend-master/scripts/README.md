# Scritps

## Create Egg file
`python setup.py clean bdist_egg`

## Execute
`export ENV=<STAGING | PROD>`
 
`python dist/revenue_jobs-0.1-py3.7.egg <command> --file_path=<file path> --db=<database> --col=<collection name> --op=insert`

Eg:
 `export ENV=PROD`
 
 `python dist/revenue_jobs-0.1-py3.7.egg base_upload --file_path='/Users/b0098480/Downloads/msisdn.txt' --db=test --col=test --op=delete`

