import { ThrowStmt } from '@angular/compiler';
import { Cause } from './cause';

export class Comment {
    id: string;
    content: string;
    owner: string;
    causeId: string;

    constructor(
        id: string,
        owner: string,
        cause: string,
        content: string
    ) {
        this.id = id;
        this.owner = owner;
        this.causeId = cause;
        this.content = content;
    }
}
