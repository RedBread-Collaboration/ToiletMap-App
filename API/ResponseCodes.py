def Succeed(req):
    return req.setResponseCode(200)


def Forbidden(req):
    return req.setResponseCode(403)


def BadRequest(req):
    return req.setResponseCode(400)