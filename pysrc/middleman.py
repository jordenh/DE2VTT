#!/usr/bin/python
import sys
import atexit
import serial
import select
import socket
import signal
import queue
import threading
import time

HOST = ''
PORT = 50002

def open_serial():
    if 'linux' in sys.platform:
        dev = '/dev/ttyUSB'
    elif 'win' in sys.platform:
        dev = '\\.\COM'

    dev += input("Enter the serial port #:")

    try:
        ser = serial.Serial(port = dev,
                baudrate = 115200,
                bytesize = 8,
                parity = "N",
                stopbits = 1)

        if not ser.isOpen():
            ser.open()

    except serial.SerialException as e:
        print(e)
        sys.exit()

    return ser

def serial_loopback():
    ser = open_serial()

    while True:
        length = ser.read()
        ser.write(length)
        print("length: ", ord(length))
        data = ser.read(ord(length))
        print(data.decode())

def tcp_loopback():
    print("Host ip addr:")
    print(socket.gethostbyname(socket.gethostname()), "\n")

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind((HOST, PORT))

    sock.listen(5)

    conn, addr = sock.accept()
    print("Connection Address", addr, "\n")

    while True:
        data = conn.recv(1024)
        if not data: break
        print("data: ", data)
        print("received data length: ", len(data))
        sent = conn.send(data)
        print("sent length: ", sent)

def tcp_serial():
    id_count = 0
    conn_map = {}
    tcp_send_queues = []
    uart_send_queue = queue.Queue()

    ser = open_serial()
    ser_thread = threading.Thread(target = serial_worker, args = (ser, tcp_send_queues, uart_send_queue))
    ser_thread.daemon = True
    ser_thread.start()

    print("Host ip addr:")
    print(socket.gethostbyname(socket.gethostname()), "\n")

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind((HOST, PORT))

    sock.listen(5)

    while True:
        conn, addr = sock.accept()

        if addr[0] in conn_map.keys():
            conn_id = conn_map[addr[0]]
        else:
            id_count+=1

            conn_id = id_count
            conn_map[addr[0]] = conn_id

        tcp_send_queue = queue.Queue()
        tcp_send_queues.append(tcp_send_queue)

        print("Connection Id:", conn_id, " Connection Address", addr, "\n")

        t = threading.Thread(target = tcp_worker, args = (conn, conn_id, tcp_send_queue, uart_send_queue))
        t.daemon = True
        t.start()

def tcp_worker(conn, conn_id, tcp_send_queue, uart_send_queue):
    try:
        oldLen = 0
        while True:
            (sread, swrite, sexec) = select.select([conn], [], [], 0)

            if sread:

                msgLen = 0
                x = b''
                data = b''
                for i in reversed(range(0, 4)):
                    tmp=conn.recv(1)
                    x+= tmp
                    msgLen = (msgLen + (ord(tmp) * (1 << i * 8)))
                    data += tmp

                # 5 is for command length, and 4 bytes of message length info
                while len(data) < (msgLen + 5):
                    oldLen = len(data)
                    data += conn.recv(msgLen)
                    print("received ", len(data), " data of ", msgLen, " so far!")
                    if oldLen == len(data):
                        break;

                if not data: break

                #Append connection id to data
                data = chr(conn_id).encode() + data
                print("data: ", data)

                uart_send_queue.put(data)

            if not tcp_send_queue.empty():
                data = tcp_send_queue.get()
                conn.send(data)
    except:
        print("catch tcp exception")
        REMOVE_TOKEN = 11
        data =   chr(0).encode() + chr(0).encode() + chr(0).encode() + chr(0).encode() + chr(REMOVE_TOKEN).encode()
        data = chr(conn_id).encode() + data
        print("data: ", data)
        uart_send_queue.put(data)

def serial_worker(ser, tcp_send_queues, uart_send_queue):
    ready = False

    while True:
        if ser.inWaiting() > 0:

            conn_id = ord(ser.read())
            print("Connection Id: " + str(conn_id))

            if conn_id == 0:
                ready = True
            else:
                msgLen = 0
                x = b''
                for i in reversed(range(0, 4)):
                    tmp=ser.read(1)
                    x+= tmp
                    msgLen = (msgLen + (ord(tmp) * (1 << i * 8)))

                print("length: ", str(msgLen))

                #Data includes the command in this code (+1)
                data = ser.read(msgLen + 1)
                print(data)

                #Push data to correct tcp queue
                tcp_send_queues[conn_id - 1].put(x)
                tcp_send_queues[conn_id - 1].put(data)

        if (not uart_send_queue.empty()) and ready:
            print("Sending data through the serial port.")
            data = uart_send_queue.get()
            ser.write(data)
            ready = False

def main():
    print("""Welcome to Middleman

This program allows you to transmit data between serial and TCP.

    The program supports three modes:
    0. Serial Loopback
    1. TCP Loopback
    2. Serial <-> TCP
    """)

    while True:
        try:
            usr_input = int(input("Select mode (0,1,2):"))

            if usr_input in [0,1,2]:
                print("")
                break
            else:
                print("Invalid mode selection")
        except ValueError:
            print("Please enter a valid integer")

    if usr_input == 0:
        serial_loopback()

    elif usr_input == 1:
        while True:
            try:
                tcp_loopback()
            except:
                print("Caught exception in tcp loopback.")
    else:
        tcp_serial()

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        sys.exit("\nUser keyboard interrupt")
