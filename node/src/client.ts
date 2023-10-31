import * as net from 'net';


const client = net.connect(30112, 'localhost', () => {
    console.log('Connected to server!');
});

client.on('data', (data) => {
});


