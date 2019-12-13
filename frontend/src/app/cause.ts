import { Time } from '@angular/common';
import { User } from './user';

export class Cause {
    id: string;
    ownerId: string;
    name: string;
    location: string;
    description: string;
    time: Time;
    commentIds: Array<string>;
    participantIds: Array<string>;
    causeType: string;
}