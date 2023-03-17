package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(null, null, Long.MIN_VALUE);

    private final Object monitor = new Object();

    public boolean propose(Bid bid) {
        synchronized (monitor) {
            if (bid.getPrice() > latestBid.getPrice()) {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }
            return false;
        }
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
