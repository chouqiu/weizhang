weizhang
========

使用img.html做测试

使用外部库，比如tesseract，需要在项目属性Java Build Path--〉libraries中引入外部jar，同时在Order and Export中将引入的jar打勾。否则会出现ClassNotFound异常


预约违章处理
http://app.stc.gov.cn:8095/TrafficPro/straffic/straffic_handlePoint.action
POST /TrafficPro/straffic/straffic_handlePoint.action HTTP/1.1
Host: app.stc.gov.cn:8095
Connection: keep-alive
Content-Length: 74
Cache-Control: max-age=0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
Origin: http://app.stc.gov.cn:8095
User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.102 Safari/537.36
Content-Type: application/x-www-form-urlencoded
Referer: http://app.stc.gov.cn:8095/TrafficPro/straffic/straffic_initInfo.action
Accept-Encoding: gzip,deflate,sdch
Accept-Language: zh-CN,zh;q=0.8
Cookie: JSESSIONID=9EC6013EF354DAA0B9D6AEAB7FF8BC08
carNo=%E7%B2%A4B053hn&hpzl=02&isCard=410305198101124514&mobile=13242960001

选预约处理地点
GET /TrafficPro/straffic/straffic_orderTime.action?radioValue=440305000000-%25E5%258D%2597%25E5%25B1%25B1%25E4%25BA%25A4%25E8%25AD%25A6%25E5%25A4%25A7%25E9%2598%259F%25E8%25BF%259D%25E6%25B3%2595%25E5%25A4%2584%25E7%2590%2586%25E7%2582%25B9 HTTP/1.1
Host: app.stc.gov.cn:8095
Connection: keep-alive
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.102 Safari/537.36
Referer: http://app.stc.gov.cn:8095/TrafficPro/straffic/straffic_handlePoint.action
Accept-Encoding: gzip,deflate,sdch
Accept-Language: zh-CN,zh;q=0.8
Cookie: JSESSIONID=9EC6013EF354DAA0B9D6AEAB7FF8BC08

选预约时间
POST /TrafficPro/straffic/straffic_orderStatus.action HTTP/1.1
Host: app.stc.gov.cn:8095
Connection: keep-alive
Content-Length: 81
Cache-Control: max-age=0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
Origin: http://app.stc.gov.cn:8095
User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.102 Safari/537.36
Content-Type: application/x-www-form-urlencoded
Referer: http://app.stc.gov.cn:8095/TrafficPro/straffic/straffic_orderTime.action?radioValue=440305000000-%25E5%258D%2597%25E5%25B1%25B1%25E4%25BA%25A4%25E8%25AD%25A6%25E5%25A4%25A7%25E9%2598%259F%25E8%25BF%259D%25E6%25B3%2595%25E5%25A4%2584%25E7%2590%2586%25E7%2582%25B9
Accept-Encoding: gzip,deflate,sdch
Accept-Language: zh-CN,zh;q=0.8
Cookie: JSESSIONID=9EC6013EF354DAA0B9D6AEAB7FF8BC08
orderValue=44515&orderTime=2014-02-15+%E6%98%9F%E6%9C%9F%E5%85%AD+11%3A00-12%3A00