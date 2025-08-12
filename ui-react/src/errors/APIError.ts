class APIError extends Error {
    details: unknown;

    constructor(message: string, details: unknown) {
        super(message);
        this.details = details;
    }

    toString(): string {
        return super.toString() + ' ' + JSON.stringify(this.details);
    }
}

export default APIError;
