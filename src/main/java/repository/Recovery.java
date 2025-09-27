package repository;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "recovery")
@NamedQueries({
    @NamedQuery(name = "Recovery.findAll", query = "SELECT r FROM Recovery r"),
    @NamedQuery(name = "Recovery.findById", query = "SELECT r FROM Recovery r WHERE r.id = :id"),
    @NamedQuery(name = "Recovery.findByToken", query = "SELECT r FROM Recovery r WHERE r.token = :token"),
    @NamedQuery(name = "Recovery.findByCreatedAt", query = "SELECT r FROM Recovery r WHERE r.createdAt = :createdAt"),
    @NamedQuery(name = "Recovery.findByExpirationAt", query = "SELECT r FROM Recovery r WHERE r.expirationAt = :expirationAt"),
    @NamedQuery(name = "Recovery.findByUsed", query = "SELECT r FROM Recovery r WHERE r.used = :used"),
    @NamedQuery(name = "Recovery.findByStatus", query = "SELECT r FROM Recovery r WHERE r.status = :status")})
public class Recovery implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "token")
    private String token;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "expiration_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationAt;
    @Column(name = "used")
    private Boolean used;
    @Size(max = 7)
    @Column(name = "status")
    private String status;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Appuser userId;

    public Recovery() {
    }

    public Recovery(Long id) {
        this.id = id;
    }

    public Recovery(Long id, String token, Date createdAt) {
        this.id = id;
        this.token = token;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpirationAt() {
        return expirationAt;
    }

    public void setExpirationAt(Date expirationAt) {
        this.expirationAt = expirationAt;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Appuser getUserId() {
        return userId;
    }

    public void setUserId(Appuser userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Recovery)) {
            return false;
        }
        Recovery other = (Recovery) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "repository.Recovery[ id=" + id + " ]";
    }
    
}
