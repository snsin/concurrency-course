package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(null, null, Long.MIN_VALUE);
    private volatile boolean isStopped = false;

    private final Object monitor = new Object();

    public boolean propose(Bid bid) {
        if (isStopped || bid.getPrice() <= latestBid.getPrice()) {
            return false;
        }
        synchronized (monitor) {
            if (isStopped || bid.getPrice() <= latestBid.getPrice()) {
                return false;
            }
            notifier.sendOutdatedMessage(latestBid);
            latestBid = bid;
            return true;
        }
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        if (!this.isStopped) {
            synchronized (monitor) {
                if (this.isStopped) {
                    return latestBid;
                }
                this.isStopped = true;
            }
        }
        return latestBid;
    }
}
