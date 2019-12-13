export class Log {
    id: string;
    username: string;
    causeId: string;
    eventType: string;
    causeName: string;

    constructor(
        id: string,
        username: string,
        causeId: string,
        eventType: string,
        causeName: string
    ) {
        this.id = id;
        this.username = username;
        this.causeId = causeId;
        this.eventType = eventType;
        this.causeName = causeName;
    }
}
