from core import core_config
import requests

def notify(title, content, level=9):
    if core_config["DEFAULT"]["gotify"] == 'True':
        ip = core_config["DEFAULT"]["notification_ip"]
        port = core_config["DEFAULT"]["notification_port"]
        token = core_config["DEFAULT"]["notification_token"]
        url = f"http://{ip}:{port}/message?token={token}"

        data = {
            'title': title,
            'message': content,
            'priority': level
        }

        requests.post(url, data = data)


if __name__ == '__main__':
    notify('test', 'content')