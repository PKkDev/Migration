export class MissingContextError extends Error {
  constructor(hook: string, provider: string) {
    super(`${hook} must be used within <${provider}>`);
    this.name = "MissingContextError";
  }
}
