def getKey(req):
    return req.getHeader('key')


def Succeed(req):
    req.setResponseCode(200)


def Created(req):
    req.setResponseCode(201)


def NoContent(req):
    req.setResponseCode(204)


def BadRequest(req):
    req.setResponseCode(400)


def Forbidden(req):
    req.setResponseCode(403)


def SetContentTypeJSON(req):
    req.setHeader('Content-Type', 'application/json')
