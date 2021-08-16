# Telegram database bot
____

Project for corporate database management using Telegram Bot. Project in development. Based on Spring and Telegram Bot API. Project work with Jenkins on AWS servers.

Currently implemented:
- [X] Uploading the [distributor's (rrc.ru)](https://b2b.rrc.ru/) price list by sending the file to the Bot. Since the price list is more than 20 MB, it must be sent in zip format.
- [X] Parsing the price list and saving it in the database. Implemented saving goods only from Cisco.
- [X] Getting the Global price of a product, using a command `/price partnumber` where `partnumber` is a model of a product.
- [X] Getting the price of a product with discount, using a command `/price partnumber -57` where `partnumber` is a model of a product and `-57` is a value of discount in percents.
- [X] Loading currency rates of the Central Bank of Russia.
- [X] Getting the price in RUB.
- [X] Automatic receipt of a distributor's price list from the Internet without sending a file to Bot.

In the plans:
- [ ] Keeping records of goods in the warehouse
- [ ] Receiving goods to the warehouse.
- [ ] Shipment of goods from the warehouse.
- [ ] Sending product balances on e-mail
- [ ] etc.
