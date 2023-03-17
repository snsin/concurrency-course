package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new  AtomicReference<>(new Bid(null,
            null,
            Long.MIN_VALUE));

    public boolean propose(Bid bid) {
        Bid outdatedBidCandidate;
        do {
            outdatedBidCandidate = latestBid.get();
            if (outdatedBidCandidate.getPrice() >= bid.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(outdatedBidCandidate, bid));
        notifier.sendOutdatedMessage(outdatedBidCandidate);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
