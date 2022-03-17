def getKey(req):
    return req.getHeader('key')


def Succeed(req):
    req.setResponseCode(200)


def Forbidden(req):
    req.setResponseCode(403)


def BadRequest(req):
    req.setResponseCode(400)

def SetResponseJSON(req):
    req.setHeader('Content-Type', 'application/json')