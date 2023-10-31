const net = require('net');

net.createClient = function(port, host) {
  return net.connect(port, host);
};