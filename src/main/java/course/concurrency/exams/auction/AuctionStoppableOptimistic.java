package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicMarkableReference<Bid> latestBid = new AtomicMarkableReference<>(new Bid(null,
            null,
            Long.MIN_VALUE),
            false);

    public boolean propose(Bid bid) {
        if (latestBid.isMarked()) {
            return false;
        }
        Bid outdatedBidCandidate;
        do {
            outdatedBidCandidate = latestBid.getReference();
            if (latestBid.isMarked() || bid.getPrice() <= outdatedBidCandidate.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(outdatedBidCandidate, bid, false, false));
        notifier.sendOutdatedMessage(outdatedBidCandidate);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        if (latestBid.isMarked()) {
            return latestBid.getReference();
        }
        Bid finalBid;
        do {
            finalBid = latestBid.getReference();
        } while (!latestBid.attemptMark(finalBid, true));
        return finalBid;
    }
}
