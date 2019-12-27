import { Time } from '@angular/common';
import { User } from './user';

export class Cause {
    id: string;
    owner: string;
    name: string;
    location: string;
    description: string;
    photoPath: string;
    date: Date;
    time: number;
    commentIds: Array<string>;
    participantIds: Array<string>;
    causeType: string;

    constructor(jsonObj: any) {
        for (let prop in jsonObj) {
            this[prop] = jsonObj[prop];
        }
    }
}
