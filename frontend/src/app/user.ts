import { Cause } from "./cause";

export class User {
    id: string;
    username: string;
    password: string;
    firstName: string;
    lastName: string;
    causes: Array<Cause>;
    ownCauses: Array<Cause>;
    authorities: Array<string>;
    email: string;
    photoPath: string;
    honor: BigInteger;

    constructor(jsonObj: any) {
        for (let prop in jsonObj) {
            this[prop] = jsonObj[prop];
        }
    }
}
