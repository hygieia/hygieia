export let API_TOKEN_LIST = [
    {
        id: '12345678901234567890',
        apiUser: 'testing',
        apiKey: 'sha512:f216973c3e3a2e34b2d6595a17254a24c278f2051' +
        +'9340d22248a143d4d5dc6b79399c099898adb8be94598d19c6520c6ea75996498860151de47b64e83c1dc75',
        expirationDt: 1656475199999,
        hashed: true
    },
    {
        id: '12345678901234567890',
        apiUser: 'testing1',
        apiKey: 'sha512:a316973c3e3a2e34b2d6595a17254a24c278' +
        +'f20519340d22248a143d4d5dc6b79399c099898adb8be94598d19c6520c6ea75996498860151de47b64e83c1dc75',
        expirationDt: 1656485199999,
        hashed: true
    }
];

export let USER_LIST = [
    {
        id: '5bf62b6a67c2ba05fcc8baa8',
        username: 'test',
        authorities: ['ROLE_USER'],
        authType: 'LDAP',
        firstName: 'test',
        lastName: 'test',
        emailAddress: 'test@example.com'
    },

    {
        id: '5bf83b6a67c2ba05fcc8baa8',
        username: 'user',
        authorities: ['ROLE_ADMIN'],
        authType: 'LDAP',
        firstName: 'test1',
        lastName: 'test1',
        emailAddress: 'test1@example.com'
    }
];
