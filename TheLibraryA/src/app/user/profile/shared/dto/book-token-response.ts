export interface BookTokenResponse {
  token: string;
  useCount: number;
  justCreated: boolean;
  expiresAt: string;
}
