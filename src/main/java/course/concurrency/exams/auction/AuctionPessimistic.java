package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;
    private final Object monitor = new Object();
    private volatile Bid latestBid = new Bid(null, null, Long.MIN_VALUE);

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            synchronized (monitor) {
                if (bid.getPrice() <= latestBid.getPrice()) {
                    return false;
                }
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
