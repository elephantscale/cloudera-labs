-- Load the file using a space character as column separator
files = LOAD 'oozieProj/files.dat' USING PigStorage (',') AS (name: chararray,size:int, month:chararray);    

-- Get files with sizes between 1,000 and 5,000 bytes
filtered_files = FILTER files BY (size > 1000) AND (size < 5000); 

-- Concatenate fields using the ':' character
STORE filtered_files INTO 'oozieProj/output' USING PigStorage (':');
