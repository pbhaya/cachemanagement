Dispatcher Caching based on Resource Types

How to implement TTL based on Resource Type ?

### Create a custom OSGI configuration.
Categorise all the page templates that are part of your platform into various groups and assess their caching requirements. 
Maintain a list of page templates & their corresponding max-age in an OSGI. A good example would be to categorise templates into 3 groups cached for 0, 900 & 3600 seconds respectively.

### Create a custom filter.  
When a request hits publisher, this Filter will evaluate the TTL(time-to-live) based on above Cache Control OSGI configuration and set the required cache-control max-age response headers. When the response is returned to end-user via various outbound systems in the architecture like Dispatcher and CDN, same response headers will be respected for caching content with right cache expiry details. We can also set max-age as 0 in order to deny caching.

For demo purpose, we have used We-Retail OOTB templates in the Cache Control OSGI.



