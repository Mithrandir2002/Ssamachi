export interface Subscription {
  id: string;
  userId: string;
  minMagnitude: number;
  region: string | null;
  active: boolean;
  createdAt: string;
}

export interface CreateSubscriptionRequest {
  minMagnitude: number;
  region: string | null;
}
